package beermaster.entry.simple;

import beermaster.entry.Entry;

import java.util.Map;
import java.util.Objects;

/**
 * An entry name is a path to a data file.
 * Path comprises delimiter "/".
 * Each entry has version and its data.
 */
public final class SimpleEntry implements Entry {
    private final String name;
    private final int version;
    private final Map<String, String> data;

    SimpleEntry(String name, int version, Map<String, String> data) {
        this.name = name;
        this.version = version;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEntry entry = (SimpleEntry) o;
        return version == entry.version && Objects.equals(name, entry.name) && Objects.equals(data, entry.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, data);
    }

    @Override
    public String toString() {
        return "SimpleEntry{" +
                "name='" + name + '\'' +
                ", version=" + version +
                ", data=" + data +
                '}';
    }
}
