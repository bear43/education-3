package beermaster.io.entry.info.file;

import beermaster.data.InfoRecord;
import beermaster.io.entry.info.DataInfoOut;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DataInfoFileOutput implements DataInfoOut {
    private final Path path;
    private final Path directories;

    public DataInfoFileOutput(Path path) {
        this.path = path;
        this.directories = path.getParent();
    }

    public DataInfoOut write(InfoRecord infoRecord) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            outputStream.writeObject(infoRecord);
        }
        return this;
    }

    @Override
    public DataInfoOut create() throws IOException {
        Files.createDirectories(directories);
        Files.createFile(path);
        return this;
    }

    @Override
    public DataInfoOut delete() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
