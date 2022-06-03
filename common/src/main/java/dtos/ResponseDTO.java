package dtos;

import responseData.Status;

import java.io.Serializable;

/**
 * Response data transfer object, contains message and status, sent from server to client
 */
public class ResponseDTO implements Serializable {
    private String message = null;
    private Status status = null;

    public String getMessage() {
        return message;
    }

    public ResponseDTO setMessage(String message) {
        this.message = message;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public ResponseDTO setStatus(Status status) {
        this.status = status;
        return this;
    }
}
