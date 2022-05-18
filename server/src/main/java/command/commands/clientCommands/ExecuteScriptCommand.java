package command.commands.clientCommands;

import command.Command;
import command.Response;

public class ExecuteScriptCommand extends Command {
    public ExecuteScriptCommand(Response response) {
        super("execute", "execute script from the specified file", response);
    }

    @Override
    public Response execute(boolean scriptMode) {
        return null;
    }
}
