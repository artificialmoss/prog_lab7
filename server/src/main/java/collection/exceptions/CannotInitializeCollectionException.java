package collection.exceptions;

/**
 * Exception thrown when either the path of the file with the collection is not set,
 * the file is not readable or the contents of the file can't be mapped onto elements of the collection
 */
public class CannotInitializeCollectionException extends IllegalArgumentException {
    public CannotInitializeCollectionException() {
        super();
    }
}
