package utils.exceptions;

/**
 * Thrown when a user indicates that they want a field to be null (and the field is allowed to be null)
 */
public class NullElementException extends NullPointerException {
    public NullElementException() {
        super();
    }
}
