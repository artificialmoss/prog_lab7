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

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
