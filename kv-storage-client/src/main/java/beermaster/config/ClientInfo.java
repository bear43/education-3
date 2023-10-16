package beermaster.config;

public class ClientInfo {
    private String name;

    public ClientInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
