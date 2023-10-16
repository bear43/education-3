package beermaster.server.handler;

import beermaster.entry.Entry;
import beermaster.entry.EntryManager;
import beermaster.server.request.FetchEntryRequest;
import beermaster.server.request.ServerRequest;
import beermaster.server.response.FetchEntryResponse;
import beermaster.server.response.ServerResponse;

import java.util.Optional;

public class FetchEntryRequestHandler implements ServerRequestHandler {

    private final EntryManager entryManager;

    public FetchEntryRequestHandler(EntryManager entryManager) {
        this.entryManager = entryManager;
    }

    @Override
    public Class<? extends ServerRequest> getInputClass() {
        return FetchEntryRequest.class;
    }

    @Override
    public Optional<? extends ServerResponse> handle(ServerRequest serverRequest) {
        FetchEntryRequest fetchEntryRequest = (FetchEntryRequest) serverRequest;
        try {
            Entry entry = entryManager.read(fetchEntryRequest.name());
            return Optional.of(new FetchEntryResponse(entry.getData()));
        } catch (IllegalStateException illegalStateException) {
            return Optional.of(new FetchEntryResponse(null));
        }
    }
}
