package com.cardplatform.domain.exception;

public class CardNotFoundException extends DomainException {

    /**
     * Constructs a new CardNotFoundException with the specified detail message.
     *
     * @param cardId the identifier of the card that was not found
     */
    public CardNotFoundException(String cardId) {
        super("Card not found: " + cardId);
    }

    /**
     * Constructs a new CardNotFoundException with the specified detail message and cause.
     *
     * @param cardId the identifier of the card that was not found
     * @param cause the cause of the exception
     */
    public CardNotFoundException(String cardId, Throwable cause) {
        super("Card not found: " + cardId, cause);
    }
}
