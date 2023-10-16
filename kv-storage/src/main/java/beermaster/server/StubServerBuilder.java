package beermaster.server;

public class StubServerBuilder implements ServerBuilder {
    @Override
    public ServerBuilder setSettings(ServerSettings settings) {
        return null;
    }

    @Override
    public ServerSettings getSettings() {
        return null;
    }

    @Override
    public Server build() {
        return new StubServer();
    }
}
