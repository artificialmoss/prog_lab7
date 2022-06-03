package command.commands.clientCommands;

import command.Command;
import command.Response;

import java.util.Map;

/**
 * Command that provides information about all commands available in the application
 */
public class HelpCommand extends Command {
    private final Map<String, Command> commandMap;
            
    private final String argumentRequirements = "Argument requirements:\n" +
            "\t{element} means creating an element via interactive input, commands with {element} do not need arguments;\n" +
            "\tid — must be a long value;\n" +
            "\tfilename — must be a valid accessible file;\n" +
            "\tbirthday — must be a valid date/datetime in the \"dd mm yyyy\" format.";

    public HelpCommand(Map<String, Command> commandMap, Response response) {
        super("help", "show available commands", response);
        this.commandMap = commandMap;
    }

    @Override
    public Response execute() {
        StringBuilder help = new StringBuilder("Available commands:\n");
        commandMap.values().stream()
                .filter(Command::isCallableFromClientConsole)
                .forEach(c -> help.append("\t")
                        .append(c.describe())
                        .append(";\n"));
        help.append("\n").append(argumentRequirements);
        return successfulResponse(help.toString());
    }
}
