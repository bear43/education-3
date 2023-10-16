package beermaster.server;

public interface ServerBuilder {
    ServerBuilder setSettings(ServerSettings settings);
    ServerSettings getSettings();
    Server build();
}
