package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;

/**
 * Show information about the collection request
 */
public class InfoRequest extends CommandRequest {
    public InfoRequest(CommandRequestDTO requestDTO) {
        super("info", requestDTO);
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        if (s.length != 1) {
            throw new WrongAmountOfArgumentsException();
        }
        return this;
    }
}
