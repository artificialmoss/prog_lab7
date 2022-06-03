package command.commands.clientCommands;

import authorization.AuthManager;
import authorization.User;
import command.Command;
import collectionData.Person;
import collection.CollectionManager;
import command.Response;
import dtos.UserDTO;

/**
 * Command for adding the element to the collection if it is smaller than the current minimal element of the collection, according to the elemen't natural order
 */
public class AddIfMinCommand extends Command {
    private final CollectionManager collectionManager;
    private Person person;
    private UserDTO userDTO;
    private User user;

    public AddIfMinCommand(CollectionManager collectionManager, Response response) {
        super("add_if_min {element}",
                "add element to the collection if it's smaller than the minimal element of the collection",
                response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute() {
        if (person.compareTo(collectionManager.getMin()) < 0) {
            String res = collectionManager.add(person, user);
           return successfulResponse(res);
        }
        return successfulResponse("Element not added: this element is not smaller than min.");
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