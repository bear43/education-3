package beermaster.data;

import java.util.HashMap;
import java.util.Map;

public interface DataDeltaResolver {
    Map<String, String> resolve(Map<String, String> oldOne, Map<String, String> newOne);
}
