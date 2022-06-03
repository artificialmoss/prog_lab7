package utils.exceptions;

/**
 * Exception thrown when the process of executing scripts encounters a recursive call
 */
public class RecursiveScriptException extends RuntimeException {
    public RecursiveScriptException() {
        super();
    }

    public RecursiveScriptException(String message) {
        super(message);
    }

    public RecursiveScriptException(Exception e) {
        super(e);
    }
}
