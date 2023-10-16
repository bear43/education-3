package beermaster.io.entry.data.file;

import beermaster.data.DataRecord;
import beermaster.mapper.Mapper;
import beermaster.mapper.RawToDataMapper;
import beermaster.data.RawRecord;
import beermaster.io.entry.data.DataIn;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public final class DataFileInput implements DataIn {
    private final Path path;
    private final Mapper<RawRecord, DataRecord> rawToDataMapper;

    public DataFileInput(Path path, Mapper<RawRecord, DataRecord> rawToDataMapper) {
        this.path = path;
        this.rawToDataMapper = rawToDataMapper;
    }

    public Collection<DataRecord> read() throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(Files.newInputStream(path))) {
            return readUntilEOF(dataInputStream);
        }
    }

    private Collection<DataRecord> readUntilEOF(DataInputStream dataInputStream) {
        Collection<DataRecord> records = new ArrayList<>();
        try {
            while (true) {
                RawRecord rawRecord = read(dataInputStream);
                records.add(rawToDataMapper.map(rawRecord));
            }
        } catch (EOFException ignored) {
            return records;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public DataRecord read(int offset) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            raf.seek(offset);
            RawRecord rawRecord = read(raf);
            return rawToDataMapper.map(rawRecord);
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    private RawRecord read(DataInput dataInput) throws IOException {
        long timestamp = dataInput.readLong();
        int keySize = dataInput.readInt();
        int valueSize = dataInput.readInt();
        byte[] keyBuffer = new byte[keySize];
        byte[] valueBuffer = new byte[valueSize];
        dataInput.readFully(keyBuffer);
        dataInput.readFully(valueBuffer);
        return new RawRecord(timestamp, keySize, valueSize, keyBuffer, valueBuffer);
    }
}
