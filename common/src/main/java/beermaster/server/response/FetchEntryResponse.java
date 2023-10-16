package beermaster.server.response;

import java.util.Map;

public record FetchEntryResponse(Map<String, String> data) implements ServerResponse {
}
