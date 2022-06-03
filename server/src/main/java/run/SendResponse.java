package run;

import command.Response;

public class SendResponse implements Runnable {
    private final ConnectionManager connectionManager;
    private Response response;

    public SendResponse(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public SendResponse setResponse(Response response) {
        this.response = response;
        return this;
    }

    public void run() {
        connectionManager.sendResponse(response);
    }
}
