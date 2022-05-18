package utils.exceptions;

/**
 * Thrown when the request doesn't correspond to an available command
 */
public class WrongRequestException extends IllegalArgumentException {
    public WrongRequestException() { super(); }
}
