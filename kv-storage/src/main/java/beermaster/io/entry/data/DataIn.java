package beermaster.io.entry.data;

import beermaster.data.DataRecord;

import java.io.*;
import java.util.Collection;

public interface DataIn {
    Collection<DataRecord> read() throws IOException;
    DataRecord read(int offset) throws IOException;
    boolean exists();
}
