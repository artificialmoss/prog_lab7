package command.commands.clientCommands;

import command.Command;
import collectionData.Person;
import collection.CollectionManager;
import command.Response;

/**
 * Command for adding the element to the collection if it is smaller than the current minimal element of the collection, according to the elemen't natural order
 */
public class AddIfMinCommand extends Command {
    private final CollectionManager collectionManager;
    private Person person;

    public AddIfMinCommand(CollectionManager collectionManager, Response response) {
        super("add_if_min {element}",
                "add element to the collection if it's smaller than the minimal element of the collection",
                response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        if (person.compareTo(collectionManager.getMin()) < 0) {
            String res = collectionManager.add(person);
            if (!scriptMode) return successfulResponse(res);
        }
        if (!scriptMode) return successfulResponse("Element not added: this element is not smaller than min.");
        return successfulResponse("");
    }

    public Command setPerson(Person person) {
        person.setId(collectionManager.generateNextId());
        person.updateCreationDate();
        this.person = person;
        return this;
    }
}