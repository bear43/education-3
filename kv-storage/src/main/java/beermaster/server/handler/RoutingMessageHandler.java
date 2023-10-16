package beermaster.server.handler;

import beermaster.server.request.ServerRequest;
import beermaster.util.SerializationHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoutingMessageHandler {
    private static final Logger log = Logger.getAnonymousLogger();
    public static final String CLASS_ALREADY_ROUTED = "Class already routed";
    public static final String NO_HANDLER = "There is no handler for class ";
    private static final String INCOMING_MESSAGE = "Incoming message from the client %s: %s";
    private static final String CLIENT_RESPONSE = "Response to the client %s request: %s";
    private final Map<Class<? extends ServerRequest>, ServerRequestHandler> handlerMap = new HashMap<>();
    private final ByteBuffer byteBuffer;

    public RoutingMessageHandler(int byteBufferCapacity) {
        this.byteBuffer = ByteBuffer.allocate(byteBufferCapacity);
    }

    public void register(ServerRequestHandler serverRequestHandler) {
        Class<? extends ServerRequest> clazz = serverRequestHandler.getInputClass();
        if (handlerMap.containsKey(clazz)) {
            throw new IllegalStateException(CLASS_ALREADY_ROUTED);
        }
        handlerMap.put(clazz, serverRequestHandler);
    }

    public boolean handle(SocketChannel socketChannel, byte[] message) {
        String string = new String(message, StandardCharsets.UTF_8).trim();
        try {
            log.log(Level.SEVERE, INCOMING_MESSAGE.formatted(socketChannel.getRemoteAddress().toString(), string));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ServerRequest serverRequest = SerializationHelper.decodeObject(message);
        ServerRequestHandler serverRequestHandler = handlerMap.get(serverRequest.getClass());
        if (serverRequestHandler == null) {
            System.err.println(NO_HANDLER + serverRequest.getClass().getSimpleName());
        } else {
            serverRequestHandler.handle(serverRequest)
                    .map(SerializationHelper::getBytes)
                    .ifPresent(bytes -> {
                        byteBuffer.clear()
                                .put(bytes)
                                .flip();
                        socketChannelSendWrapper(socketChannel);
                    });
        }
        return true;
    }

    private void socketChannelSendWrapper(SocketChannel socketChannel) {
        try {
            log.info(CLIENT_RESPONSE.formatted(socketChannel.getRemoteAddress().toString(),
                    new String(byteBuffer.array(), StandardCharsets.UTF_8).trim()));
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
