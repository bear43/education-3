package beermaster.server;

import beermaster.client.Client;
import beermaster.client.ClientBuilder;
import beermaster.server.request.FetchEntryRequest;
import beermaster.server.request.FetchServerNameRequest;
import beermaster.server.response.FetchEntryResponse;
import beermaster.server.response.FetchServerNameResponse;
import beermaster.util.SerializationHelper;

import java.io.*;
import java.util.Map;
import java.util.Optional;

public class ServerWrapper {
    private static final ServerWrapper instance = new ServerWrapper();
    public static final String ALREADY_INITED = "Already inited";
    private Client client;
    private String serverName;

    private ServerWrapper() {}

    public static ServerWrapper getInstance() {
        return instance;
    }

    public Client getClient() {
        return client;
    }

    public void init(ClientBuilder clientBuilder) {
        if (client != null) {
            throw new IllegalStateException(ALREADY_INITED);
        }
        client = clientBuilder.create();
        try {
            client.start();
            getServerName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client = null;
        serverName = null;
    }

    public Optional<Map<String, String>> fetchEntry(String name) {
        FetchEntryRequest fetchEntryRequest = new FetchEntryRequest(name);
        try {
            byte[] byteArray = SerializationHelper.getBytes(fetchEntryRequest);
            client.send(byteArray);
            return client.waitResponse()
                    .<FetchEntryResponse>map(SerializationHelper::decodeObject)
                    .map(FetchEntryResponse::data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> getServerName() {
        if (serverName != null) {
            return Optional.of(serverName);
        }
        FetchServerNameRequest fetchServerNameRequest = new FetchServerNameRequest();
        try {
            byte[] bytes = SerializationHelper.getBytes(fetchServerNameRequest);
            client.send(bytes);
            Optional<String> serverNameOpt = client.waitResponse()
                    .<FetchServerNameResponse>map(SerializationHelper::decodeObject)
                    .map(FetchServerNameResponse::name);
            serverNameOpt.ifPresent(name -> serverName = name);
            return serverNameOpt;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
