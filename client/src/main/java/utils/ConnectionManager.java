package utils;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.ScriptExecutionException;
import dtos.CommandRequestDTO;
import dtos.ResponseDTO;
import responseData.Status;
import utils.exceptions.ConnectionException;
import utils.exceptions.InvalidResponseException;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Class that handles the connection to the server (sending requests and getting response)
 */
public class ConnectionManager {
    private InetAddress host;
    private final int port;
    private DatagramSocket socket;
    private final int MAX_ATTEMPTS = 5;
    private final int TIMEOUT;
    private final int BUFFER_SIZE = 4096;
    private final ResponseManager responseManager = new ResponseManager();
    private final Serializer serializer = new Serializer();

    public ConnectionManager(int port, int TIMEOUT) {
        this.port = port;
        this.TIMEOUT = TIMEOUT;
    }

    /**
     * Creates a socket, sets socket timeout
     */
    public void start() {
        try {
            this.host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Sends command request to the server
     * @param request Request DTO
     */
    public void sendRequest(CommandRequestDTO request) {
        try {
            byte[] buf = serializeRequest(request);
            int len = buf.length;
            DatagramPacket requestPacket = new DatagramPacket(buf, len, InetAddress.getLocalHost(), port);
            socket.send(requestPacket);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Handles the command request (sends the requests and returns the response)
     * @param request Command request
     * @param scriptMode Current mode of the client
     * @return Response from the server
     */
    public ResponseDTO handleRequest(CommandRequest request, boolean scriptMode) {
        try {
            int attemptCount = MAX_ATTEMPTS;
            while (attemptCount > 0) {
                try {
                    CommandRequestDTO requestDTO = request.getDTO();
                    sendRequest(requestDTO);
                    ResponseDTO response = getResponse();
                    if (response.getStatus().equals(Status.ERROR) && scriptMode) {
                        throw new ScriptExecutionException(response.getMessage());
                    }
                    return response;
                } catch (SocketTimeoutException | PortUnreachableException | InvalidResponseException e) {
                    attemptCount--;
                    if (attemptCount != 0)
                        responseManager.showResponse("Server response time exceeded, trying to reconnect. "
                                + attemptCount + " attempts left.");
                    else throw new ConnectionException();
                } catch (IOException e) {
                    throw new ConnectionException();
                }
            }
        } catch (ConnectionException e) {
            return new ResponseDTO()
                    .setStatus(Status.ERROR)
                    .setMessage("Error — can't connect to the server. Try again or close the application.");
        } catch (InvalidResponseException e) {
            return new ResponseDTO()
                    .setStatus(Status.ERROR)
                    .setMessage("Error — invalid response from the server. Try again or close the application");
        }
        return null;
    }

    private byte[] getResponsePacket() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
        DatagramPacket responsePacket = new DatagramPacket(buf.array(), BUFFER_SIZE);
        socket.receive(responsePacket);
        return buf.array();
    }

    /**
     * Gets the entire server response
     * @return Response
     */
    public ResponseDTO getResponse() throws IOException {
        try {
            int packetCount = (Integer) serializer.deserialize(getResponsePacket());
            ByteBuffer buf = ByteBuffer.allocate(packetCount * BUFFER_SIZE);
            while (packetCount != 0) {
                buf.put(getResponsePacket());
                packetCount--;
            }
            return (ResponseDTO) serializer.deserialize(buf.array());
        } catch (ClassNotFoundException | IOException e) {
            throw new InvalidResponseException();
        }
    }

    /**
     * Serializes command request data transfer object
     * @param requestDTO Command request DTO
     * @return Resulting byte array
     * @throws IOException
     */
    public byte[] serializeRequest(CommandRequestDTO requestDTO) throws IOException {
        return serializer.serialize(requestDTO);
    }
}
