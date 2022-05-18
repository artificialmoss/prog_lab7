package command.commands.clientCommands;

import command.Command;
import collection.CollectionManager;
import command.Response;

/**
 * Command that prints birthdays of all elements that are currently stored in the collection
 */
public class PrintBirthdaysCommand extends Command {
    private final CollectionManager collectionManager;

    public PrintBirthdaysCommand(CollectionManager collectionManager, Response response) {
        super("print_birthdays",
                "show the birthday field values for all elements in the descending order",
                response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        return successfulResponse(collectionManager.descendingBirthdays());
    }
}
