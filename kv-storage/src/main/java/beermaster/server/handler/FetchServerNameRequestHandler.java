package beermaster.server.handler;

import beermaster.server.Configuration;
import beermaster.server.request.FetchServerNameRequest;
import beermaster.server.request.ServerRequest;
import beermaster.server.response.FetchServerNameResponse;
import beermaster.server.response.ServerResponse;

import java.util.Optional;

public class FetchServerNameRequestHandler implements ServerRequestHandler {

    @Override
    public Class<? extends ServerRequest> getInputClass() {
        return FetchServerNameRequest.class;
    }

    @Override
    public Optional<? extends ServerResponse> handle(ServerRequest serverRequest) {
        Configuration configuration = Configuration.getInstance();
        return Optional.of(new FetchServerNameResponse(configuration.getName()));
    }
}
