package beermaster.entry.storage;

import beermaster.data.*;
import beermaster.mapper.Mapper;
import beermaster.entry.Entry;
import beermaster.entry.simple.SimpleEntryFactory;
import beermaster.io.entry.data.DataIO;
import beermaster.io.entry.data.DataIn;
import beermaster.io.entry.data.DataOut;
import beermaster.io.entry.data.file.DataFileInput;
import beermaster.io.entry.data.file.DataFileOutput;
import beermaster.io.entry.info.DataInfoIO;
import beermaster.io.entry.info.DataInfoIn;
import beermaster.io.entry.info.DataInfoOut;
import beermaster.io.entry.info.file.DataInfoFileInput;
import beermaster.io.entry.info.file.DataInfoFileOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileStorage implements Storage {
    private static final Map<String, DataIO> dataIOMap = new ConcurrentHashMap<>();
    private static final Map<String, DataInfoIO> dataInfoIOMap = new ConcurrentHashMap<>();
    private static final String NOTHING_CHANGED = "Nothing has been changed";
    private final Mapper<RawRecord, DataRecord> rawToDataMapper;
    private final Mapper<DataRecord, RawRecord> dataToRawMapper;
    private final String root;
    private final String dataFilename;
    private final String infoFilename;
    private final Mapper<Collection<DataRecord>, Map<String, String>> dataMapper;
    private final DataDeltaResolver dataDeltaResolver;

    public FileStorage(String root, String dataFilename, String infoFilename, Mapper<Collection<DataRecord>,
            Map<String, String>> dataMapper, DataDeltaResolver dataDeltaResolver, Mapper<RawRecord, DataRecord> rawToDataMapper,
                       Mapper<DataRecord, RawRecord> dataToRawMapper) {
        this.root = root;
        this.dataFilename = dataFilename;
        this.infoFilename = infoFilename;
        this.dataMapper = dataMapper;
        this.dataDeltaResolver = dataDeltaResolver;
        this.rawToDataMapper = rawToDataMapper;
        this.dataToRawMapper = dataToRawMapper;
    }

    @Override
    public boolean doesExist(String name) {
        DataInfoIO dataInfoIO = getOrCreateDataInfoIO(name);
        return dataInfoIO.getIn().exists();
    }

    @Override
    public Entry create(Entry entry) {
        String name = entry.getName();
        DataInfoIO dataInfoIO = getOrCreateDataInfoIO(name);
        DataIO dataIO = getOrCreateDataIO(name);
        try {
            dataInfoIO.getOut()
                    .create()
                    .write(new InfoRecord(name, entry.getVersion()));
            LocalDateTime now = LocalDateTime.now();
            dataIO.getOut()
                    .create()
                    .write(mapToDataRecords(entry, now));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entry;
    }

    private Set<DataRecord> mapToDataRecords(Entry entry, LocalDateTime now) {
        return entry.getData()
                .entrySet()
                .stream()
                .map(entryRow -> new DataRecord(now, entryRow.getKey(), entryRow.getValue()))
                .collect(Collectors.toSet());
    }

    @Override
    public Entry update(Entry entry) {
        String name = entry.getName();
        DataIO dataIO = getOrCreateDataIO(name);
        DataInfoIO dataInfoIO = getOrCreateDataInfoIO(name);
        try {
            Map<String, String> dataOfTheCurrentVersion = dataMapper.map(dataIO.getIn().read());
            Map<String, String> newData = entry.getData();
            Map<String, String> delta = dataDeltaResolver.resolve(dataOfTheCurrentVersion, newData);
            if (delta.isEmpty()) {
                throw new IllegalStateException(NOTHING_CHANGED);
            }
            LocalDateTime now = LocalDateTime.now();
            Set<DataRecord> dataRecords = delta.entrySet()
                    .stream()
                    .map(mapEntry -> new DataRecord(now, mapEntry.getKey(), mapEntry.getValue()))
                    .collect(Collectors.toSet());
            InfoRecord infoRecord = new InfoRecord(name, entry.getVersion());
            dataInfoIO.getOut()
                    .write(infoRecord);
            dataIO.getOut()
                    .write(dataRecords);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entry;
    }

    @Override
    public Entry read(String name) {
        DataIO dataIO = getOrCreateDataIO(name);
        DataInfoIO dataInfoIO = getOrCreateDataInfoIO(name);
        try {
            InfoRecord infoRecord = dataInfoIO.getIn().read();
            Collection<DataRecord> dataRecords = dataIO.getIn().read();
            SimpleEntryFactory simpleEntryFactory = new SimpleEntryFactory(dataMapper);
            return simpleEntryFactory.create(infoRecord, dataRecords);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String name) {
        DataInfoIO dataInfoIO = getOrCreateDataInfoIO(name);
        DataIO dataIO = getOrCreateDataIO(name);
        dataInfoIO.getOut().delete();
        dataIO.getOut().delete();
        evictDataInfoIO(name);
        evictDataIO(name);
    }

    @Override
    public int getVersion(String name) {
        try {
            return getOrCreateDataInfoIO(name).getIn()
                    .read()
                    .version();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Entry> findAll() {
        Path rootPath = Path.of(root);
        int rootNameCount = rootPath.getNameCount();
        try {
            return Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.endsWith(infoFilename))
                    .map(path -> {
                        Path parent = path.getParent();
                        int nameCount = parent.getNameCount();
                        return "/" + parent.subpath(rootNameCount, nameCount);
                    })
                    .map(this::read)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getDataPathFor(String name) {
        String targetPath = root + name;
        return Path.of(targetPath + "/" + dataFilename);
    }

    private Path getInfoPathFor(String name) {
        String targetPath = root + name;
        return Path.of(targetPath + "/" + infoFilename);
    }

    private DataInfoIO getOrCreateDataInfoIO(String name) {
        return dataInfoIOMap.computeIfAbsent(name, ignored -> new DataInfoIO() {
            @Override
            public DataInfoIn getIn() {
                return new DataInfoFileInput(getInfoPathFor(name));
            }

            @Override
            public DataInfoOut getOut() {
                return new DataInfoFileOutput(getInfoPathFor(name));
            }
        });
    }

    private DataIO getOrCreateDataIO(String name) {
        return dataIOMap.computeIfAbsent(name, ignored -> new DataIO() {
            @Override
            public DataIn getIn() {
                return new DataFileInput(getDataPathFor(name), rawToDataMapper);
            }

            @Override
            public DataOut getOut() {
                return new DataFileOutput(getDataPathFor(name), dataToRawMapper);
            }
        });
    }

    private void evictDataIO(String name) {
        dataIOMap.remove(name);
    }

    private void evictDataInfoIO(String name) {
        dataInfoIOMap.remove(name);
    }
}
