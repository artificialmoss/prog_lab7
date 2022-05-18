package command.commands.serverCommands;

import command.Command;
import collection.CollectionManager;
import collection.JsonParser;
import command.Response;

public class SaveCommand extends Command {
    private final CollectionManager collectionManager;
    private final JsonParser parser;

    public SaveCommand(CollectionManager collectionManager, JsonParser parser, Response response) {
        super("save" , "save the collection to file", response);
        this.collectionManager = collectionManager;
        this.parser = parser;
    }

    @Override
    public Response execute(boolean scriptMode) {
        String res = collectionManager.save(parser);
        return successfulResponse(res);
    }
}
