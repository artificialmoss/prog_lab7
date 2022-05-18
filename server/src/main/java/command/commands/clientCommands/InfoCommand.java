package command.commands.clientCommands;

import command.Command;
import collection.CollectionManager;
import command.Response;

import java.time.format.DateTimeFormatter;

/**
 * Command that provides basic information about the collection (its type, size, initialization date, element size and element fields)
 */
public class InfoCommand extends Command {
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager, Response response) {
        super("info", "show information about the collection", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return successfulResponse("Collection type: " + collectionManager.getType() + "\n" +
                "Element type: " + collectionManager.getElementType() + "\n" +
                "Collection size: " + collectionManager.getSize() + "\n" +
                "Initialization date: " + collectionManager.getDate() +"\n");
    }
}
