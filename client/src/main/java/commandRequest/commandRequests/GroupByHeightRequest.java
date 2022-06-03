package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;
import dtos.UserDTO;

/**
 * Group elements by height and show the size of each group request
 */
public class GroupByHeightRequest extends CommandRequest {
    public GroupByHeightRequest(CommandRequestDTO requestDTO) {
        super("group", requestDTO, true);
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        if (s.length != 1) {
            throw new WrongAmountOfArgumentsException();
        }
        return this;
    }
}
