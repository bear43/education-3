package beermaster.client;

import java.io.IOException;
import java.util.Optional;

public class StubClient implements Client {
    @Override
    public void start() throws IOException {
        printUnsupported();
    }

    @Override
    public Optional<byte[]> waitResponse() throws IOException {
        printUnsupported();
        return Optional.empty();
    }

    @Override
    public void send(byte[] bytes) throws IOException {
        printUnsupported();
    }

    @Override
    public void close() throws IOException {
        printUnsupported();
    }

    private void printUnsupported() {
        System.err.println("Unsupported operation");
    }

}
