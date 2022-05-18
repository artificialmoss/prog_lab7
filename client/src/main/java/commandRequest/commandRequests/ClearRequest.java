package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;

/**
 * Clear the collection request
 */
public class ClearRequest extends CommandRequest {
    public ClearRequest(CommandRequestDTO requestDTO) {
        super("clear", requestDTO);
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        if (s.length != 1) {
            throw new WrongAmountOfArgumentsException();
        }
        return this;
    }
}
