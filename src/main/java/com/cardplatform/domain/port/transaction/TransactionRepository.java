package com.cardplatform.domain.port.transaction;

import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.domain.model.transaction.TransactionId;
import com.cardplatform.domain.model.card.CardId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    /**
     * Saves a transaction entity.
     *
     * @param transaction the transaction to save
     * @return the saved transaction
     */
    Transaction save(Transaction transaction);

    /**
     * Finds a transaction by its identifier.
     *
     * @param transactionId the transaction identifier
     * @return an optional containing the transaction if found
     */
    Optional<Transaction> findById(TransactionId transactionId);

    /**
     * Finds all transactions for a specific card with pagination.
     *
     * @param cardId the card identifier
     * @param pageable the pagination information
     * @return a page of transactions
     */
    Page<Transaction> findByCardId(CardId cardId, Pageable pageable);

    /**
     * Finds all transactions for a specific card.
     *
     * @param cardId the card identifier
     * @return list of transactions
     */
    List<Transaction> findByCardId(CardId cardId);

    /**
     * Counts transactions by card ID within a time window (for rate limiting).
     *
     * @param cardId the card identifier
     * @param fromTimestamp the start timestamp
     * @param toTimestamp the end timestamp
     * @return the count of transactions
     */
    long countByCardIdAndCreatedAtBetween(CardId cardId, long fromTimestamp, long toTimestamp);

}
