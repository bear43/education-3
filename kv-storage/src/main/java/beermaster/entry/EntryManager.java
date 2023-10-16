package beermaster.entry;

import beermaster.entry.storage.Storage;

import java.util.Collection;

public final class EntryManager {
    private static final String ALREADY_EXISTS = "Entry already exists";
    private static final String NOT_EXISTS = "Entry does not exist";
    private static final String OUT_OF_DATE_VERSION = "Given entry version is out of date. It should be greater than the last version %d by 1 (%d)";
    private static final String WRONG_VERSION = "Wrong version. It should be equal to lastVersion + 1 (%d)";
    private final Storage storage;

    public EntryManager(Storage storage) {
        this.storage = storage;
    }

    public synchronized Entry create(Entry entry) {
        if (storage.doesExist(entry.getName())) {
            throw new IllegalStateException(ALREADY_EXISTS);
        }
        return storage.create(entry);
    }

    public synchronized Entry read(String name) {
        if (!storage.doesExist(name)) {
            throw new IllegalStateException(NOT_EXISTS);
        }
        return storage.read(name);
    }

    public synchronized Entry update(Entry entry) {
        if (!storage.doesExist(entry.getName())) {
            throw new IllegalStateException(NOT_EXISTS);
        }
        int lastVersion = storage.getVersion(entry.getName());
        if (entry.getVersion() <= lastVersion) {
            throw new IllegalStateException(OUT_OF_DATE_VERSION.formatted(lastVersion, lastVersion + 1));
        }
        if (entry.getVersion() > lastVersion + 1) {
            throw new IllegalStateException(WRONG_VERSION.formatted(lastVersion + 1));
        }
        return storage.update(entry);
    }

    public synchronized void delete(String name) {
        if (!storage.doesExist(name)) {
            return;
        }
        storage.delete(name);
    }

    public synchronized Collection<Entry> findAll() {
        return storage.findAll();
    }
}
