package beermaster.server.nio;

import beermaster.context.ApplicationContext;
import beermaster.server.Server;
import beermaster.server.ServerSettings;
import beermaster.server.handler.RoutingMessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Logger;

public final class NioServer implements Server {
    private static final Logger log = Logger.getAnonymousLogger();
    private static final int CLIENT_MESSAGE_CAPACITY = 1024;
    private static final String CLOSED_CONNECTION_TO_A_CLIENT = "Closed connection with the client %s";
    private static final String START_LISTENING = "Started listening on port %d...%n";
    private static final String SERVER_SOCKET_CHANNEL_CLOSED = "ServerSocketChannel closed successfully";
    private static final String CONNECTION_ACCEPT = "New connection from the client %s has been accepted";
    private final ServerSettings settings;
    private final ApplicationContext applicationContext;
    private final RoutingMessageHandler routingMessageHandler;

    public NioServer(ServerSettings settings, ApplicationContext applicationContext, RoutingMessageHandler routingMessageHandler) {
        this.settings = settings;
        this.applicationContext = applicationContext;
        this.routingMessageHandler = routingMessageHandler;
    }


    public void start() throws IOException {
        try (Selector selector = Selector.open()) {
            try (ServerSocketChannel serverSocket = configureServerSocketChannel(selector)) {
                ByteBuffer buffer = ByteBuffer.allocate(CLIENT_MESSAGE_CAPACITY);
                log.info(START_LISTENING.formatted(settings.port()));
                while (applicationContext.isRunning()) {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKeys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        buffer.clear();
                        if (!handleSelectionKey(selector, serverSocket, buffer, key)) {
                            applicationContext.shutdown();
                            return;
                        }
                        iter.remove();
                    }
                }
            } finally {
                log.info(SERVER_SOCKET_CHANNEL_CLOSED);
            }
            selector.keys()
                    .forEach(selectionKey -> {
                        try {
                            selectionKey.channel().close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private boolean handleSelectionKey(Selector selector, ServerSocketChannel serverSocket, ByteBuffer buffer, SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            configureSocketChannel(selector, serverSocket);
        }
        if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            int r = client.read(buffer);
            if (r == -1) {
                SocketAddress remoteAddress = client.getRemoteAddress();
                client.close();
                log.info(CLOSED_CONNECTION_TO_A_CLIENT.formatted(remoteAddress));
            } else {
                return routingMessageHandler.handle(client, buffer.array());
            }
        }
        return true;
    }

    private ServerSocketChannel configureServerSocketChannel(Selector selector) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(settings.port()));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        return serverSocket;
    }

    private void configureSocketChannel(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        log.info(CONNECTION_ACCEPT.formatted(client.getRemoteAddress().toString()));
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
}
