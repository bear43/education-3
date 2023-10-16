package beermaster.mapper;

import beermaster.data.DataRecord;
import beermaster.data.RawRecord;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RawToDataMapper implements Mapper<RawRecord, DataRecord> {

    private final ZoneId zoneId;

    public RawToDataMapper(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public RawToDataMapper() {
        this.zoneId =ZoneId.of("Europe/Moscow");
    }

    public DataRecord map(RawRecord rawRecord) {
        LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(rawRecord.timestamp()), zoneId);
        return new DataRecord(timestamp,
                new String(rawRecord.key(), StandardCharsets.UTF_8),
                rawRecord.valueSize() == 0 ? null : new String(rawRecord.value(), StandardCharsets.UTF_8));
    }
}
