package beermaster.server;

public class Configuration {
    private static final Configuration instance = new Configuration();
    private String name;
    private int port;

    private Configuration() {
    }

    public static Configuration getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
