package beermaster.client;

import java.util.Optional;

public interface MessageHandler {
    Optional<byte[]> handleMessage(byte[] array);
}
