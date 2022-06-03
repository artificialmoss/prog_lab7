package run;

import command.Response;
import dtos.CommandRequestDTO;
import log.Log;
import org.apache.logging.log4j.Logger;
import utils.Serializer;
import utils.exceptions.ConnectionException;
import utils.exceptions.InvalidResponseException;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Class that manages connection to client (receiving requests and sending responses)
 */
public class ConnectionManager {
    private DatagramChannel datagramChannel;
    private volatile SocketAddress clientAddress;
    private final int BUFFER_SIZE = 4096;
    private final Serializer serializer = new Serializer();
    private Selector selector;
    private final Logger logger = Log.getLogger();

    /**
     * Creates a channel
     * @param port Port
     */
    public void startConnection(int port) {
        try {
            SocketAddress localAddress = new InetSocketAddress(port);
            datagramChannel = DatagramChannel.open();
            datagramChannel.bind(localAddress);
            datagramChannel.configureBlocking(false);
            selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);
        } catch (BindException e) {
            logger.fatal("This port is already in use. The application will be closed." +
                    "Restart the application with another port.");
            System.exit(0);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Receives command request
     * @return Command request
     */
    public synchronized CommandRequestDTO receiveCommandRequest() {
        try {
            if (selector.select() > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext(); ) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isReadable()) {
                        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
                        clientAddress = datagramChannel.receive(buf);
                        datagramChannel.register(selector, SelectionKey.OP_WRITE);
                        try {
                            CommandRequestDTO requestDTO = deserializeRequest(buf);
                            logger.info("New " + requestDTO.getName() + " command request has been received.");
                            return requestDTO;
                        } catch (ClassNotFoundException | IOException e) {
                            logger.error("Couldn't deserialize the received request");
                            throw new ConnectionException();
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Couldn't receive the command request.");
            throw new ConnectionException();
        }
        return null;
    }

    /**
     * Sends response
     * @param response Response
     */
    public synchronized void sendResponse(Response response) {
        try {
            if (selector.select() > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext(); ) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isWritable()) {
                        sendResponse(serializeResponse(response), response.getClientAddress());
                        datagramChannel.register(selector, SelectionKey.OP_READ);
                        logger.info("The response to the command request has been sent to the client.");
                    }
                }
            }
        } catch (InvalidResponseException e) {
            logger.error("Couldn't serialize the response.");
        } catch (IOException e) {
            logger.error("Couldn't send the response to command request to client.");
            throw new ConnectionException();
        }
    }

    private synchronized void sendResponse(ByteBuffer[] response, SocketAddress clientAddress) {
        try {
            Integer size = response.length;
            datagramChannel.send(ByteBuffer.wrap(serializer.serialize(size)), clientAddress);
            for (ByteBuffer buf : response) {
                datagramChannel.send(buf, clientAddress);
            }
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    private synchronized ByteBuffer[] serializeResponse(Response response) {
        try {
            byte[] tmp = serializer.serialize(response.toDTO());
            int size = (int) Math.ceil((double) tmp.length / BUFFER_SIZE);
            ByteBuffer[] res = new ByteBuffer[size];
            int start = 0;
            int end = BUFFER_SIZE;
            for (int i = 0; i < size; i++) {
                res[i] = ByteBuffer.wrap(Arrays.copyOfRange(tmp, start, end));
                start += BUFFER_SIZE;
                end += BUFFER_SIZE;
            }
            return res;
        } catch (IOException e) {
            throw new InvalidResponseException();
        }
    }

    private synchronized CommandRequestDTO deserializeRequest(ByteBuffer buf) throws IOException, ClassNotFoundException {
        return (CommandRequestDTO) serializer.deserialize(buf.array());
    }

    public synchronized SocketAddress getClientAddress() {
        return clientAddress;
    }
}
