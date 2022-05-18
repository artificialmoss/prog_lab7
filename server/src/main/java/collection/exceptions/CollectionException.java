package collection.exceptions;

import java.util.NoSuchElementException;

/**
 * Exception thrown when the command that is supposed to change the state of the collection can't be
 * executed due to the collection's current state
 */
public class CollectionException extends NoSuchElementException {
    public CollectionException(String message) {
        super(message);
    }
}
