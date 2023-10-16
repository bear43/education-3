package beermaster.io.entry.data;

import beermaster.data.DataRecord;

import java.io.IOException;
import java.util.Collection;

public interface DataOut {
    DataOut write(Collection<DataRecord> records) throws IOException;
    DataOut create() throws IOException;
    DataOut delete();
}
