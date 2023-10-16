package beermaster.mapper;

import beermaster.data.DataRecord;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataToMapMapper implements Mapper<Collection<DataRecord>, Map<String, String>> {

    private static final String TOMBSTONE = "TOMBSTONE";

    @Override
    public Map<String, String> map(Collection<DataRecord> source) {
        Map<String, String> map = new HashMap<>();
        for (DataRecord dataRecord : source) {
            if (TOMBSTONE.equals(dataRecord.value())) {
                map.remove(dataRecord.key());
                continue;
            }
            map.put(dataRecord.key(), dataRecord.value());
        }
        return map;
    }
}
