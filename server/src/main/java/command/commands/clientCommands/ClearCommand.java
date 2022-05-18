package command.commands.clientCommands;


import command.Command;
import collection.CollectionManager;
import command.Response;

/**
 * Command for clearing the collection
 */
public class ClearCommand extends Command {
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager, Response response) {
        super("clear", "clear the collection", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        String res = collectionManager.clear();
        if (!scriptMode) return successfulResponse(res);
        return successfulResponse("");
    }
}
