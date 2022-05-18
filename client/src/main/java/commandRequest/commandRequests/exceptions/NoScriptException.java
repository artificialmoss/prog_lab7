package commandRequest.commandRequests.exceptions;

/**
 * Exception thrown when the script in the execute script command either doesn't exist or can't be accessed
 */
public class NoScriptException extends IllegalArgumentException {
    public NoScriptException() {
        super();
    }

    public NoScriptException(String message) {
        super(message);
    }
}
