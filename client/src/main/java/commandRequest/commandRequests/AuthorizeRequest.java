package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;
import utils.UserReader;

public class AuthorizeRequest extends CommandRequest {
    private final UserReader userReader;

    public AuthorizeRequest(UserReader userReader, CommandRequestDTO requestDTO) {
        super("authorize", requestDTO);
        this.userReader = userReader;
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        return this;
    }

    public CommandRequest setArgs() throws WrongAmountOfArgumentsException, WrongArgumentException {
        setUser(userReader.readUser());
        return this;
    }
}
