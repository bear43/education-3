package beermaster.mapper;

import beermaster.data.DataRecord;
import beermaster.data.RawRecord;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

public class DataToRawMapper implements Mapper<DataRecord, RawRecord> {
    private final ZoneId zoneId;

    public DataToRawMapper(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public DataToRawMapper() {
        this.zoneId =ZoneId.of("Europe/Moscow");
    }

    public RawRecord map(DataRecord dataRecord) {
        long timestamp = dataRecord.timestamp()
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli();
        byte[] key = dataRecord.key().getBytes(StandardCharsets.UTF_8);
        byte[] value = dataRecord.value() == null ? new byte[0] : dataRecord.value().getBytes(StandardCharsets.UTF_8);
        return new RawRecord(timestamp, key.length, value.length, key, value);
    }
}
