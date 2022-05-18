package command.commands.clientCommands;

import command.Command;
import collection.CollectionManager;
import command.Response;

/**
 * Command for grouping elements of the collection by their heights and showing the size of each group
 */
public class GroupByHeightCommand extends Command {
    private final CollectionManager collectionManager;

    public GroupByHeightCommand(CollectionManager collectionManager, Response response) {
        super("group", "group elements by their height, show the size of each group", response);
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(boolean scriptMode) {
        return successfulResponse(collectionManager.groupByHeight());
    }
}