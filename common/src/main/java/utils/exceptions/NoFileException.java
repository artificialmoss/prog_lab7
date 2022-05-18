package utils.exceptions;

/**
 * Exception thrown when the files for initializing or saving the collection can't be accessed
 */
public class NoFileException extends NullPointerException {
    public NoFileException() {
        super();
    }

    public NoFileException(String s) {
        super(s);
    }
}
