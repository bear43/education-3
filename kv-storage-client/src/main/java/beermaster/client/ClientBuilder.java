package beermaster.client;

import beermaster.config.Configuration;

public interface ClientBuilder {
    Configuration getConfiguration();
    void setConfiguration(Configuration configuration);
    Client create();
}
