package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;
import utils.ResponseManager;

/**
 * Close the client application request
 */
public class ExitRequest extends CommandRequest implements Runnable {
    private final ResponseManager responseManager;

    public ExitRequest(ResponseManager responseManager, CommandRequestDTO requestDTO) {
        super("exit", requestDTO);
        this.responseManager = responseManager;
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        if (s.length != 1) {
            throw new WrongAmountOfArgumentsException();
        }
        return this;
    }

    @Override
    public void run() {
        responseManager.showResponse("Closing application...");
        System.exit(0);
    }
}
