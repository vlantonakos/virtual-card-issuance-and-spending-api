package com.cardplatform.infrastructure.persistence.repository.transaction;

import com.cardplatform.infrastructure.persistence.entity.transaction.TransactionEntity;
import com.cardplatform.domain.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    /**
     * Find all transactions for a specific card with pagination, ordered by creation date descending.
     *
     * @param cardId The card identifier.
     * @param pageable The pagination information.
     * @return A page of transactions.
     */
    Page<TransactionEntity> findByCardIdOrderByCreatedAtDesc(UUID cardId, Pageable pageable);

    /**
     * Find all transactions for a specific card, ordered by creation date descending.
     *
     * @param cardId The card identifier.
     * @return A list of transactions.
     */
    List<TransactionEntity> findByCardIdOrderByCreatedAtDesc(UUID cardId);

    /**
     * Count transactions by card ID and type within a time window (for rate limiting).
     *
     * @param cardId The card identifier.
     * @param type The transaction type.
     * @param fromTimestamp The start timestamp.
     * @param toTimestamp The end timestamp.
     * @return The count of transactions.
     */
    long countByCardIdAndTypeAndCreatedAtBetween(UUID cardId, TransactionType type, Instant fromTimestamp,
                                                 Instant toTimestamp);

}
