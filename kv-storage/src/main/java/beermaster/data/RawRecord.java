package beermaster.data;


public record RawRecord(long timestamp, int keySize, int valueSize, byte[] key, byte[] value) {
}
