package command.commands.clientCommands;

import authorization.AuthManager;
import command.Command;
import command.Response;
import dtos.UserDTO;

public class RegisterCommand extends Command {
    private final AuthManager authManager;
    private UserDTO userDTO;

    public RegisterCommand(AuthManager authManager, Response response) {
        super("register", response);
        this.authManager = authManager;
    }

    @Override
    public Command setUserDTO(UserDTO user) {
        this.userDTO = user;
        return this;
    }

    @Override
    public Response execute() {
        if (!authManager.matchUsername(userDTO)) {
            return authManager.registerUser(userDTO, getClientAddress());
        } else
            return errorResponse("Error — user with this username already exists.");
    }
}
