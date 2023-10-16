package beermaster.data;

import java.util.HashMap;
import java.util.Map;

public final class DataDeltaResolverImpl implements DataDeltaResolver {

    private static final String TOMBSTONE = "TOMBSTONE";
    private record UpdatedOrDeleted(Map<String, String> deleted, Map<String, String> updated) {
    }

    public Map<String, String> resolve(Map<String, String> oldOne, Map<String, String> newOne) {
        UpdatedOrDeleted updatedOrDeleted = findUpdatedOrDeleted(oldOne, newOne);
        Map<String, String> created = findCreated(oldOne, newOne);
        HashMap<String, String> changesUnion = new HashMap<>();
        changesUnion.putAll(created);
        changesUnion.putAll(updatedOrDeleted.updated());
        changesUnion.putAll(updatedOrDeleted.deleted());
        return changesUnion;
    }

    private UpdatedOrDeleted findUpdatedOrDeleted(Map<String, String> oldOne, Map<String, String> newOne) {
        Map<String, String> deleted = new HashMap<>();
        Map<String, String> updated = new HashMap<>();
        for (String oldKey : oldOne.keySet()) {
            if (!newOne.containsKey(oldKey)) {
                deleted.put(oldKey, TOMBSTONE);
                continue;
            }
            if ((oldOne.get(oldKey) == null && newOne.get(oldKey) != null)) {
                updated.put(oldKey, newOne.get(oldKey));
                continue;
            }
            if (!oldOne.get(oldKey).equals(newOne.get(oldKey))) {
                updated.put(oldKey, newOne.get(oldKey));
            }
        }
        return new UpdatedOrDeleted(deleted, updated);
    }

    private Map<String, String> findCreated(Map<String, String> oldOne, Map<String, String> newOne) {
        Map<String, String> created = new HashMap<>();
        for (String newKey : newOne.keySet()) {
            if (!oldOne.containsKey(newKey)) {
                created.put(newKey, newOne.get(newKey));
            }
        }
        return created;
    }
}
