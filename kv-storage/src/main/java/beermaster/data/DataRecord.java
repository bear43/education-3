package beermaster.data;

import java.time.LocalDateTime;

public record DataRecord(LocalDateTime timestamp, String key, String value) {
}
