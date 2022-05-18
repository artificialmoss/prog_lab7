package command.commands.clientCommands;

import command.Command;
import command.Response;
import log.Log;
import run.CommandManager;

public class ChangeModeCommand extends Command {
    private final CommandManager commandManager;

    public ChangeModeCommand(CommandManager commandManager, Response response) {
        super("change_mode", null, response);
        this.commandManager = commandManager;
    }

    @Override
    public boolean isCallable() {
        return true;
    }

    @Override
    public Command setMode(Boolean scriptMode) {
        commandManager.setScriptMode(scriptMode);
        Log.getLogger().info("Script mode has been set to " + scriptMode);
        return this;
    }

    @Override
    public Response execute(boolean scriptMode) {
        return successfulResponse("");
    }
}
