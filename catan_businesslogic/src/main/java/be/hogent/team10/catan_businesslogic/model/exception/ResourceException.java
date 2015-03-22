package be.hogent.team10.catan_businesslogic.model.exception;

/**
 *
 * @author HP
 */
public class ResourceException extends Exception {

    /**
     * Creates a new instance of
     * <code>ResourceException</code> without detail message.
     */
    public ResourceException() {
    }

    /**
     * Constructs an instance of
     * <code>ResourceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ResourceException(String msg) {
        super(msg);
    }
}
