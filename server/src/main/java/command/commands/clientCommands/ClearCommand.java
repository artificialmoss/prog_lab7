package command.commands.clientCommands;


import authorization.AuthManager;
import authorization.User;
import command.Command;
import collection.CollectionManager;
import command.Response;
import dtos.UserDTO;

/**
 * Command for clearing the collection
 */
public class ClearCommand extends Command {
    private final CollectionManager collectionManager;
    private UserDTO userDTO;
    private User user;

    public ClearCommand(CollectionManager collectionManager, Response response) {
        super("clear", "clear the collection", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute() {
        String res = collectionManager.clear(user);
        return successfulResponse(res);
    }

    @Override
    public Command setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
        return this;
    }

    @Override
    public Command matchUser(AuthManager authManager) {
        user = authManager.matchUser(userDTO);
        return this;
    }
}
