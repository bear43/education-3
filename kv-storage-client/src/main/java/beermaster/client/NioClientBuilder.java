package beermaster.client;

import beermaster.config.Configuration;

public class NioClientBuilder implements ClientBuilder {

    private Configuration configuration;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Client create() {
        return new NioClient(configuration.getServerInfo().getPort());
    }
}
