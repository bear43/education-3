package beermaster.server.handler;

import beermaster.server.request.ServerRequest;
import beermaster.server.response.ServerResponse;

import java.util.Optional;

public interface ServerRequestHandler {
    Class<? extends ServerRequest> getInputClass();
    Optional<? extends ServerResponse> handle(ServerRequest serverRequest);
}
