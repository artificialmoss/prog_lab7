package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;
import dtos.UserDTO;

/**
 * Show all elements' birthdays request
 */
public class PrintBirthdaysRequest extends CommandRequest {
    public PrintBirthdaysRequest(CommandRequestDTO requestDTO) {
        super("print_birthdays", requestDTO, true);
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        if (s.length != 1) {
            throw new WrongAmountOfArgumentsException();
        }
        return this;
    }
}
