package command.commands.clientCommands;

import command.Command;
import collectionData.Person;
import collection.CollectionManager;
import command.Response;

/**
 * Command for updating the element of the collection with the specified id
 */
public class UpdateByIdCommand extends Command {
    private final CollectionManager collectionManager;
    private Long id;
    private Person person;

    public UpdateByIdCommand(CollectionManager collectionManager, Response response) {
        super("update id", "update the element with the specified id", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        if (collectionManager.getById(id) != null) {
            collectionManager.updateById(id, person);
            if (!scriptMode) return successfulResponse("Element with id " + id + " has been updated.");
            return successfulResponse("");
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
}
