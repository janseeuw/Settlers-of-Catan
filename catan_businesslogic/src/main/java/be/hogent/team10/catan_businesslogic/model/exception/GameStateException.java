package be.hogent.team10.catan_businesslogic.model.exception;

/**
 *
 * @author HP
 */
public class GameStateException extends Exception {

    /**
     * Creates a new instance of
     * <code>GameStateException</code> without detail message.
     */
    public GameStateException() {
    }

    /**
     * Constructs an instance of
     * <code>GameStateException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public GameStateException(String msg) {
        super(msg);
    }
}
