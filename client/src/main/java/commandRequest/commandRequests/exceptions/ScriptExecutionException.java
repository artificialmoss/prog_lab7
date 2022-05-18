package commandRequest.commandRequests.exceptions;

/**
 * Exception thrown when one of the commands in the script can't be executed (mainly due to the collection's
 * state)
 */
public class ScriptExecutionException extends RuntimeException {
    public ScriptExecutionException(String message) {
        super(message);
    }
}
