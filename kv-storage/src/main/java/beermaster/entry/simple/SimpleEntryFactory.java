package beermaster.entry.simple;

import beermaster.data.DataRecord;
import beermaster.data.InfoRecord;
import beermaster.entry.Entry;
import beermaster.entry.EntryFactory;
import beermaster.mapper.Mapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleEntryFactory implements EntryFactory {

    private static final String WRONG_DATA_TYPE = "Can handle only Map<String, String>, but object was of class: ";
    private static final String WRONG_ENTRY_TYPE = "Can handle only entries of Map<String, String> but the given map contains the entry either with null/non-string key or with non-null non-string value";
    private final Mapper<Collection<DataRecord>, Map<String, String>> dataMapper;

    public SimpleEntryFactory(Mapper<Collection<DataRecord>, Map<String, String>> dataMapper) {
        this.dataMapper = dataMapper;
    }

    @Override
    public Entry create(String name, Object data) {
        Map<String, String> map = validateData(data);
        return new SimpleEntry(name, 1, Map.copyOf(map));
    }

    @Override
    public Entry create(String name, int version, Object data) {
        Map<String, String> map = validateData(data);
        return new SimpleEntry(name, version, Map.copyOf(map));
    }

    @Override
    public Entry create(InfoRecord info, Collection<DataRecord> data) {
        Map<String, String> dataMap = dataMapper.map(data);
        return new SimpleEntry(info.name(), info.version(), dataMap);
    }

    private Map<String, String> validateData(Object data) {
        if (!(data instanceof Map<?, ?> map)) {
            Object dataClass = data == null ? "null" : data.getClass();
            throw new IllegalStateException(WRONG_DATA_TYPE + dataClass);
        }
        boolean doesContainWrongMapEntry = doesContainWrongMapEntry(map);
        if (doesContainWrongMapEntry) {
            throw new IllegalStateException(WRONG_ENTRY_TYPE);
        }
        return (Map<String, String>) map;
    }

    private boolean doesContainWrongMapEntry(Map<?, ?> map) {
        return map.entrySet()
                .stream()
                .map(Map.Entry.class::cast)
                .anyMatch(entry -> entry.getKey() == null
                        || !entry.getKey().getClass().equals(String.class)
                        || (entry.getValue() != null && !entry.getValue().getClass().equals(String.class)));
    }
}
