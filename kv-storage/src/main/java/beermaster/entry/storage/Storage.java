package beermaster.entry.storage;

import beermaster.entry.Entry;

import java.util.Collection;

public interface Storage {
    boolean doesExist(String name);
    Entry create(Entry entry);
    Entry update(Entry entry);
    Entry read(String name);
    void delete(String name);
    int getVersion(String name);
    Collection<Entry> findAll();
}
