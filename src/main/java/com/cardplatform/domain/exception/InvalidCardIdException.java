package com.cardplatform.domain.exception;

public class InvalidCardIdException extends DomainException {

    /**
     * Constructs a new InvalidCardIdException with the specified card ID.
     *
     * @param cardId the invalid card ID
     */
    public InvalidCardIdException(String cardId) {
        super("Invalid card ID format: " + cardId);
    }

    /**
     * Constructs a new InvalidCardIdException with the specified card ID and cause.
     *
     * @param cardId the invalid card ID
     * @param cause the cause of the exception
     */
    public InvalidCardIdException(String cardId, Throwable cause) {
        super("Invalid card ID format: " + cardId, cause);
    }

}
