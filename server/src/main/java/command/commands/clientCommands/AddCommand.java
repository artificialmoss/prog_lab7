package command.commands.clientCommands;

import authorization.AuthManager;
import authorization.User;
import command.Command;
import collectionData.Person;
import collection.CollectionManager;
import command.Response;
import dtos.UserDTO;

/**
 * Command for adding elements to the collection
 */
public class AddCommand extends Command {
    private final CollectionManager collectionManager;
    private Person person;
    private UserDTO userDTO;
    private User user;

    public AddCommand(CollectionManager collectionManager, Response response) {
        super("add {element}", "add new element to the collection", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute() {
        String res = collectionManager.add(person, user);
        return successfulResponse(res);
    }

    public Command setPerson(Person person) {
        person.updateCreationDate();
        this.person = person;
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
