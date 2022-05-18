package command.commands.clientCommands;

import command.Command;
import collectionData.Person;
import collection.CollectionManager;
import command.Response;

/**
 * Command for adding elements to the collection
 */
public class AddCommand extends Command {
    private final CollectionManager collectionManager;
    private Person person;

    public AddCommand(CollectionManager collectionManager, Response response) {
        super("add {element}", "add new element to the collection", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        String res = collectionManager.add(person);
        if (!scriptMode) return successfulResponse(res);
        return successfulResponse("");
    }

    public Command setPerson(Person person) {
        person.setId(collectionManager.generateNextId());
        person.updateCreationDate();
        this.person = person;
        return this;
    }
}
