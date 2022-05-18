package commandRequest.commandRequests;

import commandRequest.CommandRequest;
import commandRequest.commandRequests.exceptions.WrongAmountOfArgumentsException;
import commandRequest.commandRequests.exceptions.WrongArgumentException;
import dtos.CommandRequestDTO;

/**
 * Change script mode request, used for running the execute script command. Can't be called by user by its name
 */
public class ChangeScriptModeRequest extends CommandRequest {
    private boolean scriptMode = true;
    public ChangeScriptModeRequest(CommandRequestDTO requestDTO) {
        super("change_mode", requestDTO);
    }

    public CommandRequest setScriptMode(boolean scriptMode) {
        this.scriptMode = scriptMode;
        return this;
    }

    @Override
    public CommandRequest setArgs(String[] s) throws WrongAmountOfArgumentsException, WrongArgumentException {
        return this;
    }

    @Override
    public Boolean getScriptMode() {
        return scriptMode;
    }
}
