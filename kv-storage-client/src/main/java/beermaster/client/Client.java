package beermaster.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public interface Client extends Closeable {
    void start() throws IOException;
    Optional<byte[]> waitResponse() throws IOException;
    void send(byte[] bytes) throws IOException;
}
