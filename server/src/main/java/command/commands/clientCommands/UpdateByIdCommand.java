package command.commands.clientCommands;

import authorization.AuthManager;
import authorization.User;
import command.Command;
import collectionData.Person;
import collection.CollectionManager;
import command.Response;
import dtos.UserDTO;

/**
 * Command for updating the element of the collection with the specified id
 */
public class UpdateByIdCommand extends Command {
    private final CollectionManager collectionManager;
    private Long id;
    private Person person;
    private UserDTO userDTO;
    private User user;

    public UpdateByIdCommand(CollectionManager collectionManager, Response response) {
        super("update id", "update the element with the specified id", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute() {
        if (collectionManager.getById(id) != null) {
            String res = collectionManager.updateById(id, person, user);
            return successfulResponse(res);
        }
        return errorResponse("Element with id " + id + " doesn't exist.");
    }

    @Override
    public Command setPerson(Person person) {
        person.setId(id);
        person.updateCreationDate();
        this.person = person;
        return this;
    }

    @Override
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
        person.setCreatorId(user.getId());
        return this;
    }
}
