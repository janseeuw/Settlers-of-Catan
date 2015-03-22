package be.hogent.team10.catan_businesslogic.model.exception;

/**
 *
 * @author HP
 */
public class ObjectNotFoundException extends Exception {

    public ObjectNotFoundException() {
    }

    /**
     *
     * @param msg the detail message.
     */
    public ObjectNotFoundException(String msg) {
        super(msg);
    }
}
