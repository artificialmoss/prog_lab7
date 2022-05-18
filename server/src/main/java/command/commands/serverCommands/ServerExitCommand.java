package command.commands.serverCommands;

import command.Command;
import command.Response;
import log.Log;
import org.apache.logging.log4j.Logger;

public class ServerExitCommand extends Command {
    private final Logger logger = Log.getLogger();

    public ServerExitCommand(Response response) {
        super("exit", "close the program without saving", response);
    }

    @Override
    public Response execute(boolean scriptMode) {
        logger.info("The server application will be closed.");
        System.exit(0);
        return null;
    }
}
