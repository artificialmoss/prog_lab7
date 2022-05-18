package command;

import dtos.ResponseDTO;
import responseData.Status;

public class Response {
    private String message;
    private Status status = Status.ERROR;
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
}
