package com.cardplatform.domain.model;

import com.cardplatform.domain.model.enums.TransactionType;
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
public class Transaction {

    /**
     * This field represents the unique identifier of the transaction.
     */
    private TransactionId id;

    /**
     * This field represents the card identifier associated with this transaction.
     */
    private CardId cardId;

    /**
     * This field represents the type of transaction (SPEND, TOPUP).
     */
    private TransactionType type;

    /**
     * This field represents the amount of the transaction.
     */
    private BigDecimal amount;

    /**
     * This field represents the timestamp when the transaction was created.
     */
    private Instant createdAt;

    /**
     * Creates a new transaction with the specified parameters.
     *
     * @param cardId the card identifier
     * @param type the transaction type
     * @param amount the transaction amount
     * @return a new Transaction instance
     */
    public static Transaction create(CardId cardId, TransactionType type, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setId(TransactionId.generate());
        transaction.setCardId(cardId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCreatedAt(Instant.now());
        return transaction;
    }

}
