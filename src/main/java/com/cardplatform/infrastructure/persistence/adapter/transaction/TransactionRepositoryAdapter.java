package com.cardplatform.infrastructure.persistence.adapter.transaction;

import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.domain.model.transaction.TransactionId;
import com.cardplatform.domain.model.enums.TransactionType;
import com.cardplatform.domain.port.transaction.TransactionRepository;
import com.cardplatform.infrastructure.persistence.entity.card.CardEntity;
import com.cardplatform.infrastructure.persistence.entity.transaction.TransactionEntity;
import com.cardplatform.infrastructure.persistence.repository.card.CardJpaRepository;
import com.cardplatform.infrastructure.persistence.repository.transaction.TransactionJpaRepository;
import com.cardplatform.infrastructure.web.mapper.transaction.TransactionEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionRepositoryAdapter implements TransactionRepository {

    /**
     * JPA repository for Transaction entities.
     */
    private final TransactionJpaRepository transactionJpaRepository;

    /**
     * JPA repository for Card entities.
     */
    private final CardJpaRepository cardJpaRepository;

    /**
     * Mapper to convert between Transaction domain objects and entities.
     */
    private final TransactionEntityMapper transactionEntityMapper;

    /**
     * Saves a transaction, establishing its association with a card.
     *
     * @param transaction the transaction to save
     * @return the saved transaction domain object
     * @throws IllegalStateException if the associated card is not found
     */
    @Override
    public Transaction save(Transaction transaction) {
        log.debug("Saving transaction with ID: {}", transaction.getId());

        // Get the card entity to establish the relationship
        CardEntity cardEntity = cardJpaRepository.findById(transaction.getCardId().getValue())
                .orElseThrow(() -> new IllegalStateException("Card not found: " + transaction.getCardId()));

        TransactionEntity transactionEntity = transactionEntityMapper.mapToEntity(transaction);
        transactionEntity.setCard(cardEntity);

        TransactionEntity savedEntity = transactionJpaRepository.save(transactionEntity);
        Transaction savedTransaction = transactionEntityMapper.mapToDomain(savedEntity);

        log.debug("Successfully saved transaction with ID: {}", savedTransaction.getId());
        return savedTransaction;
    }

    /**
     * Finds a transaction by its identifier.
     *
     * @param transactionId the transaction identifier
     * @return an Optional containing the transaction if found, otherwise empty
     */
    @Override
    public Optional<Transaction> findById(TransactionId transactionId) {
        log.debug("Finding transaction by ID: {}", transactionId);

        return transactionJpaRepository.findById(transactionId.getValue())
                .map(transactionEntityMapper::mapToDomain);
    }

    /**
     * Finds transactions for a specific card, paginated and sorted by creation date descending.
     *
     * @param cardId the card identifier
     * @param pageable pagination information
     * @return a page of transactions for the card
     */
    @Override
    public Page<Transaction> findByCardId(CardId cardId, Pageable pageable) {
        log.debug("Finding transactions by card ID: {} with pagination", cardId);

        Page<TransactionEntity> entityPage = transactionJpaRepository
                .findByCardIdOrderByCreatedAtDesc(cardId.getValue(), pageable);

        return entityPage.map(transactionEntityMapper::mapToDomain);
    }

    /**
     * Finds all transactions for a specific card, sorted by creation date descending.
     *
     * @param cardId the card identifier
     * @return list of all transactions for the card
     */
    @Override
    public List<Transaction> findByCardId(CardId cardId) {
        log.debug("Finding all transactions by card ID: {}", cardId);

        List<TransactionEntity> entities = transactionJpaRepository
                .findByCardIdOrderByCreatedAtDesc(cardId.getValue());

        return transactionEntityMapper.mapToDomain(entities);
    }

    /**
     * Counts the number of SPEND transactions for a card between two timestamps.
     *
     * @param cardId the card identifier
     * @param fromTimestamp the start of the time window (inclusive), in milliseconds since epoch
     * @param toTimestamp the end of the time window (inclusive), in milliseconds since epoch
     * @return the count of SPEND transactions within the time window
     */
    @Override
    public long countByCardIdAndCreatedAtBetween(CardId cardId, long fromTimestamp, long toTimestamp) {
        log.debug("Counting SPEND transactions for card ID: {} between {} and {}",
                cardId, fromTimestamp, toTimestamp);

        return transactionJpaRepository.countByCardIdAndTypeAndCreatedAtBetween(cardId.getValue(),
                TransactionType.SPEND, Instant.ofEpochMilli(fromTimestamp), Instant.ofEpochMilli(toTimestamp));
    }

}
