package beermaster.io.entry.info.file;

import beermaster.data.InfoRecord;
import beermaster.io.entry.info.DataInfoIn;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DataInfoFileInput implements DataInfoIn {

    private final Path path;

    public DataInfoFileInput(Path path) {
        this.path = path;
    }

    public InfoRecord read() throws IOException, ClassNotFoundException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (InfoRecord) objectInputStream.readObject();
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }
}
