package beermaster.entry;

import java.io.Serializable;
import java.util.Map;

public interface Entry extends Serializable {
    String getName();
    int getVersion();
    Map<String, String> getData();
}
