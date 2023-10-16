package beermaster.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.logging.Logger;

public class NioClient implements Client {
    private static final Logger log = Logger.getAnonymousLogger();
    public static final int DEFAULT_BUFFER_CAPACITY = 1000;
    private static final String CONNECTION_CLOSED_UNEXPECTEDLY = "Connection should be opened but it is closed";
    private static final String CONNECTION_SUCCESSFUL = "Connection has been established";
    private static final String CONNECTION_CLOSED = "Connection closed successfully";
    private final int port;
    private final ByteBuffer byteBuffer;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean isConnectionOpened;

    public NioClient(int port, int bufferCapacity) {
        this.port = port;
        this.byteBuffer = ByteBuffer.allocate(bufferCapacity);
    }

    public NioClient(int port) {
        this(port, DEFAULT_BUFFER_CAPACITY);
    }

    @Override
    public void start() throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(port));
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        isConnectionOpened = true;
        log.info(CONNECTION_SUCCESSFUL);
    }

    @Override
    public void send(byte[] bytes) throws IOException {
        if (!isConnectionOpened) {
            log.info(CONNECTION_CLOSED_UNEXPECTEDLY);
            return;
        }
        byteBuffer.clear().put(bytes);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }

    @Override
    public Optional<byte[]> waitResponse() throws IOException {
        if (!isConnectionOpened) {
            log.info(CONNECTION_CLOSED_UNEXPECTEDLY);
            return Optional.empty();
        }
        selector.select();
        byteBuffer.clear();
        socketChannel.read(byteBuffer);
        return Optional.of(byteBuffer.array());
    }

    @Override
    public void close() throws IOException {
        if (isConnectionOpened) {
            log.info(CONNECTION_CLOSED);
            socketChannel.close();
            selector.close();
        }
    }
}
