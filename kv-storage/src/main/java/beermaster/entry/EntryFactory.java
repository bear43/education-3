package beermaster.entry;

import beermaster.data.DataRecord;
import beermaster.data.InfoRecord;

import java.util.Collection;

public interface EntryFactory {
    Entry create(String name, Object data);
    Entry create(String name, int version, Object data);
    Entry create(InfoRecord info, Collection<DataRecord> data);
}
