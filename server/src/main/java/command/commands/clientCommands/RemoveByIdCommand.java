package command.commands.clientCommands;

import command.Command;
import collection.CollectionManager;
import command.Response;

/**
 * Command that removes element with the specified id from the collection
 */
public class RemoveByIdCommand extends Command {
    private final CollectionManager collectionManager;
    private Long id;

    public RemoveByIdCommand(CollectionManager collectionManager, Response response) {
        super("remove id", "remove the element with the specified id", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        String res = collectionManager.removeById(id);
        if (!scriptMode) return successfulResponse(res);
        return successfulResponse("");
    }

    public Command setId(Long id) {
        this.id = id;
        return this;
    }
}
