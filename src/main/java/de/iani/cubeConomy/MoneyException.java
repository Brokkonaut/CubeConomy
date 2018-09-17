package de.iani.cubeConomy;

/**
 * This exception is thrown when some money operation could not be executed
 */
public class MoneyException extends Exception {
    private static final long serialVersionUID = -6082070205715678059L;

    public MoneyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoneyException(String message) {
        super(message);
    }
}
