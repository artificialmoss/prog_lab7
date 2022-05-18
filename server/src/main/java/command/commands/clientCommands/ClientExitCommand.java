package command.commands.clientCommands;

import command.Command;
import command.Response;

public class ClientExitCommand extends Command {
    public ClientExitCommand(Response response) {
        super("exit", "close the client application", response);
    }

    @Override
    public Response execute(boolean scriptMode) {
        return null;
    }
}
