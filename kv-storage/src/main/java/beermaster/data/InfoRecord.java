package beermaster.data;

import java.io.Serializable;

public record InfoRecord(String name, int version) implements Serializable {
}
