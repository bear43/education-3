package beermaster.context.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapContext {
    private final Map<ContextKey, Object> map;

    public MapContext() {
        this.map = new HashMap<>();
    }

    public void put(ContextKey key, Object value) {
        map.put(key, value);
    }

    public <T> Optional<T> get(ContextKey key) {
        return Optional.ofNullable((T) map.get(key));
    }

    public void remove(ContextKey key) {
        map.remove(key);
    }
}
