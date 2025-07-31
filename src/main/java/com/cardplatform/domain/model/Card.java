package com.cardplatform.domain.model;

import com.cardplatform.domain.model.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Card {

    /**
     * This field represents the unique identifier of the card.
     */
    private CardId id;

    /**
     * This field represents the name of the cardholder.
     */
    private String cardholderName;

    /**
     * This field represents the current balance of the card.
     */
    private BigDecimal balance;

    /**
     * This field represents the timestamp when the card was created.
     */
    private Instant createdAt;

    /**
     * This field represents the current status of the card (ACTIVE, BLOCKED).
     */
    private CardStatus status;

    /**
     * This field represents the version for optimistic locking.
     */
    private Long version;

    /**
     * Creates a new card with the specified parameters.
     *
     * @param cardholderName the name of the cardholder
     * @param initialBalance the initial balance of the card
     * @return a new Card instance
     */
    public static Card create(String cardholderName, BigDecimal initialBalance) {
        Card card = new Card();
        card.setId(CardId.generate());
        card.setCardholderName(cardholderName);
        card.setBalance(initialBalance);
        card.setCreatedAt(Instant.now());
        card.setStatus(CardStatus.ACTIVE);
        card.setVersion(0L);
        return card;
    }

    /**
     * Spends the specified amount from the card balance.
     *
     * @param amount the amount to spend
     * @throws IllegalStateException if card is not active or insufficient balance
     */
    public void spend(BigDecimal amount) {
        if (status != CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Adds the specified amount to the card balance.
     *
     * @param amount the amount to add
     * @throws IllegalStateException if card is not active
     */
    public void topUp(BigDecimal amount) {
        if (status != CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Blocks the card.
     */
    public void block() {
        this.status = CardStatus.BLOCKED;
    }

    /**
     * Activates the card.
     */
    public void activate() {
        this.status = CardStatus.ACTIVE;
    }

}
