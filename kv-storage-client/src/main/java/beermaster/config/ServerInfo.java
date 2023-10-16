package beermaster.config;

public class ServerInfo {
    private int port;

    public ServerInfo(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "port=" + port +
                '}';
    }
}
