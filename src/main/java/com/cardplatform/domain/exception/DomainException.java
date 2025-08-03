package com.cardplatform.domain.exception;

public abstract class DomainException extends RuntimeException {

    /**
     * Constructs a new DomainException with the specified detail message.
     *
     * @param message the detail message
     */
    protected DomainException(String message) {
        super(message);
    }

    /**
     * Constructs a new DomainException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }

}
