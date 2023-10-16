package beermaster.config;

import java.util.Map;

public class Configuration {
    private static final Configuration instance = new Configuration();
    public static final int DEFAULT_SERVER_PORT = 7777;
    public static final String DEFAULT_CLIENT_NAME = "kv-storage-client";
    private final ClientInfo clientInfo;
    private final ServerInfo serverInfo;

    private Configuration() {
        this.clientInfo = new ClientInfo(DEFAULT_CLIENT_NAME);
        this.serverInfo = new ServerInfo(DEFAULT_SERVER_PORT);
    }

    public static Configuration getInstance() {
        return instance;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "clientInfo=" + clientInfo +
                ", serverInfo=" + serverInfo +
                '}';
    }
}
