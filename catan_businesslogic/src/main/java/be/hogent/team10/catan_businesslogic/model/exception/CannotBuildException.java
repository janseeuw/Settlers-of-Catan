package be.hogent.team10.catan_businesslogic.model.exception;

/**
 *
 * @author HP
 */
public class CannotBuildException extends Exception {

    /**
     * Creates a new instance of
     * <code>CannotBuildException</code> without detail message.
     */
    public CannotBuildException() {
    }

    /**
     * Constructs an instance of
     * <code>CannotBuildException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CannotBuildException(String msg) {
        super(msg);
    }
}
