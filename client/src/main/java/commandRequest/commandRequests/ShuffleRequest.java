package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;

/**
 * Shuffle the collection request
 */
public class ShuffleRequest extends CommandRequest {
    public ShuffleRequest(CommandRequestDTO requestDTO) {
        super("shuffle", requestDTO);
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        if (s.length != 1) {
            throw new WrongAmountOfArgumentsException();
        }
        return this;
    }
}
