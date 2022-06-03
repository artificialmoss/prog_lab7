package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;
import dtos.UserDTO;
import utils.UserReader;

public class RegisterRequest extends CommandRequest {
    private UserDTO user;
    private final UserReader userReader;

    public RegisterRequest(UserReader userReader, CommandRequestDTO requestDTO) {
        super("register", requestDTO);
        this.userReader = userReader;
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        setArgs();
        return this;
    }

    @Override
    public CommandRequest setArgs() throws WrongAmountOfArgumentsException, WrongArgumentException {
        setUser(userReader.readUser());
        return this;
    }
}
