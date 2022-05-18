package command.commands.clientCommands;

import command.Command;
import collection.CollectionManager;
import command.Response;

/**
 * Command for showing all current contents of the collection
 */
public class ShowCommand extends Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager, Response response) {
        super("show", "show all elements in the collection", response);
        this.collectionManager = collectionManager;
    }

    public Response execute(boolean scriptMode) {
        return successfulResponse(collectionManager.show());
    }
}
