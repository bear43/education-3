package beermaster.server;

import java.io.IOException;

public class StubServer implements Server {
    @Override
    public void start() throws IOException {
        System.err.println("Unsupported operation");
    }
}
