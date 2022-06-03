package command.commands.clientCommands;

import authorization.AuthManager;
import authorization.User;
import command.Command;
import collection.CollectionManager;
import command.Response;
import dtos.UserDTO;

/**
 * Command that removes element with the specified id from the collection
 */
public class RemoveByIdCommand extends Command {
    private final CollectionManager collectionManager;
    private Long id;
    private UserDTO userDTO;
    private User user;

    public RemoveByIdCommand(CollectionManager collectionManager, Response response) {
        super("remove id", "remove the element with the specified id", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute() {
        String res = collectionManager.removeById(id, user);
        return successfulResponse(res);
    }

    public Command setId(Long id) {
        this.id = id;
        return this;
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
