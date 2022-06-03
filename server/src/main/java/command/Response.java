package command;

import dtos.ResponseDTO;
import responseData.Status;

import java.net.SocketAddress;

public class Response {
    private String message;
    private Status status = Status.ERROR;
    private SocketAddress clientAddress;
    private final ResponseDTO responseDTO = new ResponseDTO();

    public Response setResponse(String message) {
        this.message = message;
        return this;
    }

    public Response setSuccessfulResponse(String message) {
        setResponse(message);
        this.status = Status.OK;
        return this;
    }

    public Response setError(String message) {
        setResponse(message);
        this.status = Status.ERROR;
        return this;
    }

    public ResponseDTO toDTO() {
        responseDTO.setMessage(message);
        responseDTO.setStatus(status);
        return responseDTO;
    }

    public String getMessage() {
        return message;
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }

    public Response setClientAddress(SocketAddress clientAddress) {
        this.clientAddress = clientAddress;
        return this;
    }
}
