package com.cardplatform.domain.service;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.domain.model.enums.TransactionType;
import com.cardplatform.domain.port.card.CardRepository;
import com.cardplatform.domain.port.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardDomainService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    private static final int RATE_LIMIT_WINDOW_MINUTES = 1;
    private static final int MAX_SPENDS_PER_MINUTE = 5;

    /**
     * Creates a new card with initial balance.
     *
     * @param cardholderName the name of the cardholder
     * @param initialBalance the initial balance
     * @return the created card
     */
    @Transactional
    public Card createCard(String cardholderName, BigDecimal initialBalance) {
        log.info("Creating new card for cardholder: {}", cardholderName);

        Card card = Card.create(cardholderName, initialBalance);
        Card savedCard = cardRepository.save(card);

        // Create initial top-up transaction if there's an initial balance
        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            Transaction initialTransaction = Transaction.create(
                    savedCard.getId(),
                    TransactionType.TOPUP,
                    initialBalance
            );
            transactionRepository.save(initialTransaction);
            log.info("Created initial top-up transaction for card: {}", savedCard.getId());
        }

        log.info("Successfully created card with ID: {}", savedCard.getId());
        return savedCard;
    }

    /**
     * Processes a spend transaction on a card.
     *
     * @param cardId the card identifier
     * @param amount the amount to spend
     * @return the updated card
     * @throws IllegalStateException if card is not found, not active, or has insufficient balance
     * @throws IllegalArgumentException if rate limit is exceeded
     */
    @Transactional
    public Card spendFromCard(CardId cardId, BigDecimal amount) {
        log.info("Processing spend transaction for card: {}, amount: {}", cardId, amount);

        // Check rate limiting
        checkRateLimit(cardId);

        // Get card with pessimistic lock to prevent race conditions
        Card card = cardRepository.findByIdWithLock(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        // Process the spend (domain logic)
        card.spend(amount);

        // Save the updated card
        Card updatedCard = cardRepository.save(card);

        // Create spend transaction record
        Transaction spendTransaction = Transaction.create(cardId, TransactionType.SPEND, amount);
        transactionRepository.save(spendTransaction);

        log.info("Successfully processed spend transaction for card: {}", cardId);
        return updatedCard;
    }

    /**
     * Processes a top-up transaction on a card.
     *
     * @param cardId the card identifier
     * @param amount the amount to top up
     * @return the updated card
     * @throws IllegalStateException if card is not found or not active
     */
    @Transactional
    public Card topUpCard(CardId cardId, BigDecimal amount) {
        log.info("Processing top-up transaction for card: {}, amount: {}", cardId, amount);

        // Get card with pessimistic lock
        Card card = cardRepository.findByIdWithLock(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        // Process the top-up (domain logic)
        card.topUp(amount);

        // Save the updated card
        Card updatedCard = cardRepository.save(card);

        // Create top-up transaction record
        Transaction topUpTransaction = Transaction.create(cardId, TransactionType.TOPUP, amount);
        transactionRepository.save(topUpTransaction);

        log.info("Successfully processed top-up transaction for card: {}", cardId);
        return updatedCard;
    }

    /**
     * Retrieves a card by its identifier.
     *
     * @param cardId the card identifier
     * @return the card
     * @throws IllegalStateException if card is not found
     */
    public Card getCard(CardId cardId) {
        log.debug("Retrieving card: {}", cardId);
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));
    }

    /**
     * Retrieves transaction history for a card with pagination.
     *
     * @param cardId the card identifier
     * @param pageable pagination information
     * @return page of transactions
     * @throws IllegalStateException if card is not found
     */
    public Page<Transaction> getTransactionHistory(CardId cardId, Pageable pageable) {
        log.debug("Retrieving transaction history for card: {}", cardId);

        // Verify card exists
        if (!cardRepository.existsById(cardId)) {
            throw new IllegalStateException("Card not found: " + cardId);
        }

        return transactionRepository.findByCardId(cardId, pageable);
    }

    /**
     * Blocks a card.
     *
     * @param cardId the card identifier
     * @return the updated card
     * @throws IllegalStateException if card is not found
     */
    @Transactional
    public Card blockCard(CardId cardId) {
        log.info("Blocking card: {}", cardId);

        Card card = cardRepository.findByIdWithLock(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        card.block();
        Card updatedCard = cardRepository.save(card);

        log.info("Successfully blocked card: {}", cardId);
        return updatedCard;
    }

    /**
     * Activates a card.
     *
     * @param cardId the card identifier
     * @return the updated card
     * @throws IllegalStateException if card is not found
     */
    @Transactional
    public Card activateCard(CardId cardId) {
        log.info("Activating card: {}", cardId);

        Card card = cardRepository.findByIdWithLock(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        card.activate();
        Card updatedCard = cardRepository.save(card);

        log.info("Successfully activated card: {}", cardId);
        return updatedCard;
    }

    /**
     * Checks if the rate limit for spend transactions is exceeded.
     *
     * @param cardId the card identifier
     * @throws IllegalArgumentException if rate limit is exceeded
     */
    private void checkRateLimit(CardId cardId) {
        long currentTime = Instant.now().toEpochMilli();
        long windowStart = currentTime - (RATE_LIMIT_WINDOW_MINUTES * 60 * 1000);

        long spendCount = transactionRepository.countByCardIdAndCreatedAtBetween(
                cardId, windowStart, currentTime
        );

        if (spendCount >= MAX_SPENDS_PER_MINUTE) {
            throw new IllegalArgumentException(
                    String.format("Rate limit exceeded. Maximum %d spends per minute allowed.", MAX_SPENDS_PER_MINUTE)
            );
        }
    }

}
