package command.commands.clientCommands;

import authorization.AuthManager;
import command.Command;
import command.Response;
import dtos.UserDTO;

public class AuthorizeCommand extends Command {
    private final AuthManager authManager;
    private UserDTO userDTO;

    public AuthorizeCommand(AuthManager authManager, Response response) {
        super("authorize", response);
        this.authManager = authManager;
    }

    @Override
    public Command setUserDTO(UserDTO user) {
        this.userDTO = user;
        return this;
    }

    @Override
    public Response execute() {
        if (authManager.matchUser(userDTO) != null) {
            return successfulResponse("You have been successfully authorized.");
        }
        if (authManager.matchUsername(userDTO)) {
            return errorResponse("You have entered the wrong password. Try again.");
        }
        return errorResponse("No user with username " + userDTO.getName() + " found.");
    }
}
