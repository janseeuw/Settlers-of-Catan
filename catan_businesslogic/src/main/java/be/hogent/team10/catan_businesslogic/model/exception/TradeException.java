package be.hogent.team10.catan_businesslogic.model.exception;

/**
 *
 * @author HP
 */
public class TradeException extends Exception {

    /**
     * Creates a new instance of
     * <code>TradeException</code> without detail message.
     */
    public TradeException() {
    }

    /**
     * Constructs an instance of
     * <code>TradeException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public TradeException(String msg) {
        super(msg);
    }
}
