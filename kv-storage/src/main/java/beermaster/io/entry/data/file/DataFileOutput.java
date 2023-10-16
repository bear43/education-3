package beermaster.io.entry.data.file;

import beermaster.mapper.DataToRawMapper;
import beermaster.data.DataRecord;
import beermaster.data.RawRecord;
import beermaster.io.entry.data.DataOut;
import beermaster.mapper.Mapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class DataFileOutput implements DataOut {
    private final Path path;
    private final Path directories;
    private final Mapper<DataRecord, RawRecord> dataToRawMapper;

    public DataFileOutput(Path path, Mapper<DataRecord, RawRecord> dataToRawMapper) {
        this.path = path;
        this.directories = path.getParent();
        this.dataToRawMapper = dataToRawMapper;
    }

    public DataOut write(Collection<DataRecord> records) throws IOException {
        long length = Files.size(path);
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw")) {
            raf.seek(length);
            for (DataRecord record : records) {
                RawRecord map = dataToRawMapper.map(record);
                write(raf, map);
            }
        }
        return this;
    }

    @Override
    public DataOut create() throws IOException {
        Files.createDirectories(directories);
        Files.createFile(path);
        return this;
    }

    @Override
    public DataOut delete() {
        try {
            Files.delete(path);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void write(DataOutput dataOutput, RawRecord rawRecord) throws IOException {
        dataOutput.writeLong(rawRecord.timestamp());
        dataOutput.writeInt(rawRecord.keySize());
        dataOutput.writeInt(rawRecord.valueSize());
        dataOutput.write(rawRecord.key());
        dataOutput.write(rawRecord.value());
    }
}
