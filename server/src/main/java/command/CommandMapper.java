package command;

import dtos.CommandRequestDTO;

import java.util.Map;

/**
 * Maps the command request DTO to a corresponding command
 */
public class CommandMapper {
    private final Map<String, Command> commands;

    public CommandMapper(Map<String, Command> commands) {
        this.commands = commands;
    }

    /**
     *
     * @param commandRequest Command request DTO
     * @return Corresponding command
     */
    public Command getCommand(CommandRequestDTO commandRequest) {
        if (commandRequest == null) return null;
        Command command = commands.get(commandRequest.getName());
        if (command != null) {
            command.setId(commandRequest.getId());
            command.setBirthday(commandRequest.getBirthday());
            command.setPerson(commandRequest.getPerson());
            command.setMode(commandRequest.getScriptMode());
            command.setUserDTO(commandRequest.getUser());
        }
        return command;
    }
}
