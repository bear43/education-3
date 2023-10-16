package beermaster.client;

import beermaster.config.Configuration;

public class StubClientBuilder implements ClientBuilder {

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
    }

    @Override
    public Client create() {
        return new StubClient();
    }
}
