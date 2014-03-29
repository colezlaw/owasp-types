package org.owasp.types;

/**
 * {@code ParseException} is the parent exception for any parsing failures
 * for the types.
 * 
 * @author colezlaw
 */
public class ParseException extends RuntimeException {

    /**
     * Constructs a new runtime exception with null as its detail message.
     * The cause is not initialized, and may subsequently be initialized by
     * a call to {@link Throwable#initCause(java.lang.Throwable)}.
     */
    public ParseException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by
     * a call to {@link Throwable#initCause(java.lang.Throwable)}.
     * 
     * @param message the detail message.
     * @param cause the cause. (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public ParseException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * Note that the detail message associated with cause is not automatically incorporated in
     * this runtime exception's detail message.
     * 
     * @param message the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *   (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by
     * a call to {@link Throwable#initCause(java.lang.Throwable)}.
     * 
     * @param message
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which typically
     * contains the class and detail message of {@code cause}). This constructor
     * is useful for runtime exceptions that are little more than wrappers for other throwables.
     * 
     * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     * (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParseException(Throwable cause) {
        super(cause);
    }
}
