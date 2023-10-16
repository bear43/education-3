package beermaster.io.entry.info;

import beermaster.data.InfoRecord;

import java.io.IOException;

public interface DataInfoIn {
    InfoRecord read() throws IOException, ClassNotFoundException;
    boolean exists();
}
