package de.iani.cubeConomy;

/**
 * This exception is thrown when the access to the underlying database failed.
 */
public class MoneyDatabaseException extends Exception {
    private static final long serialVersionUID = 4846355819979186408L;

    public MoneyDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoneyDatabaseException(String message) {
        super(message);
    }

}
