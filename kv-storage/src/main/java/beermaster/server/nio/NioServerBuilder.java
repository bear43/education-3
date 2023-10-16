package beermaster.server.nio;

import beermaster.context.ApplicationContext;
import beermaster.server.Configuration;
import beermaster.server.Server;
import beermaster.server.ServerBuilder;
import beermaster.server.ServerSettings;
import beermaster.server.handler.RoutingMessageHandler;

public class NioServerBuilder implements ServerBuilder {

    private ServerSettings settings;
    private ApplicationContext applicationContext;
    private RoutingMessageHandler routingMessageHandler;

    @Override
    public Server build() {
        Configuration configuration = Configuration.getInstance();
        configuration.setName(settings.name());
        configuration.setPort(settings.port());
        return new NioServer(settings, applicationContext, routingMessageHandler);
    }

    public ServerSettings getSettings() {
        return settings;
    }

    public ServerBuilder setSettings(ServerSettings settings) {
        this.settings = settings;
        return this;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public NioServerBuilder setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public RoutingMessageHandler getRoutingMessageHandler() {
        return routingMessageHandler;
    }

    public NioServerBuilder setRoutingMessageHandler(RoutingMessageHandler routingMessageHandler) {
        this.routingMessageHandler = routingMessageHandler;
        return this;
    }
}
