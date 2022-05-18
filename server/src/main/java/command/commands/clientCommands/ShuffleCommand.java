package command.commands.clientCommands;

import command.Command;
import collection.CollectionManager;
import command.Response;

/**
 * Command for shuffling the collection
 */
public class ShuffleCommand extends Command {
    private final CollectionManager collectionManager;

    public ShuffleCommand(CollectionManager collectionManager, Response response) {
        super("shuffle", "shuffle the collection", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        collectionManager.shuffle();
        if (!scriptMode) return successfulResponse("The collection has been shuffled.");
        return successfulResponse("");
    }
}
