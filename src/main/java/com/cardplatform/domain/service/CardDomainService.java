package com.cardplatform.domain.service;

import com.cardplatform.domain.exception.CardNotFoundException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CardDomainService {

    /**
     * Repository for CRUD operations on {@link Card} entities.
     */
    private final CardRepository cardRepository;

    /**
     * Repository for CRUD operations on {@link Transaction} entities.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Time window in minutes for rate limiting spend transactions.
     */
    private static final int RATE_LIMIT_WINDOW_MINUTES = 1;

    /**
     * Maximum number of spend transactions allowed per rate limit window.
     */
    private static final int MAX_SPENDS_PER_MINUTE = 5;

    /**
     * Creates a new card with the specified cardholder name and initial balance.
     * Also creates an initial top-up transaction if the initial balance is greater than zero.
     *
     * @param cardholderName the name of the cardholder
     * @param initialBalance the initial balance to fund the card
     * @return the created and persisted {@link Card}
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
     * Processes a spend transaction by deducting the specified amount from the card balance.
     * Enforces rate limiting to prevent excessive spends within a short time window.
     *
     * @param cardId the identifier of the card to spend from
     * @param amount the amount to spend
     * @return the updated {@link Card} after the spend
     * @throws IllegalStateException    if the card does not exist, is not active, or insufficient funds
     * @throws IllegalArgumentException if the rate limit for spends is exceeded
     */
    @Transactional
    public Card spendFromCard(CardId cardId, BigDecimal amount) {
        log.info("Processing spend transaction for card: {}, amount: {}", cardId, amount);

        // Check rate limiting
        checkRateLimit(cardId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        // Process the spend
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
     * Processes a top-up transaction by adding the specified amount to the card balance.
     *
     * @param cardId the identifier of the card to top up
     * @param amount the amount to add to the card balance
     * @return the updated {@link Card} after the top-up
     * @throws IllegalStateException if the card does not exist or is not active
     */
    @Transactional
    public Card topUpCard(CardId cardId, BigDecimal amount) {
        log.info("Processing top-up transaction for card: {}, amount: {}", cardId, amount);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        // Process the top-up
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
     * Retrieves the card by its unique identifier.
     *
     * @param cardId the identifier of the card
     * @return the {@link Card} entity
     * @throws CardNotFoundException if the card does not exist
     */
    public Card getCard(CardId cardId) {
        log.debug("Retrieving card: {}", cardId);
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId.toString()));
    }

    /**
     * Retrieves the paginated transaction history for a given card.
     *
     * @param cardId   the identifier of the card
     * @param pageable pagination information such as page number and size
     * @return a {@link Page} of {@link Transaction} objects
     * @throws IllegalStateException if the card does not exist
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
     * Blocks the card to prevent further transactions.
     *
     * @param cardId the identifier of the card to block
     * @return the updated {@link Card} after blocking
     * @throws IllegalStateException if the card does not exist
     */
    @Transactional
    public Card blockCard(CardId cardId) {
        log.info("Blocking card: {}", cardId);

        // Verify card exists
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        card.block();
        Card updatedCard = cardRepository.save(card);

        log.info("Successfully blocked card: {}", cardId);
        return updatedCard;
    }

    /**
     * Activates the card, allowing transactions to be processed.
     *
     * @param cardId the identifier of the card to activate
     * @return the updated {@link Card} after activation
     * @throws IllegalStateException if the card does not exist
     */
    @Transactional
    public Card activateCard(CardId cardId) {
        log.info("Activating card: {}", cardId);

        // Verify card exists
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalStateException("Card not found: " + cardId));

        card.activate();
        Card updatedCard = cardRepository.save(card);

        log.info("Successfully activated card: {}", cardId);
        return updatedCard;
    }

    /**
     * Checks whether the number of spend transactions within the rate limit window
     * has exceeded the maximum allowed threshold.
     *
     * @param cardId the identifier of the card
     * @throws IllegalArgumentException if the rate limit has been exceeded
     */
    private void checkRateLimit(CardId cardId) {
        long currentTime = Instant.now().toEpochMilli();
        long windowStart = currentTime - (RATE_LIMIT_WINDOW_MINUTES * 60 * 1000);

        long spendCount = transactionRepository.countByCardIdAndCreatedAtBetween(cardId, windowStart, currentTime);

        if (spendCount >= MAX_SPENDS_PER_MINUTE) {
            throw new IllegalArgumentException(
                    String.format("Rate limit exceeded. Maximum %d spends per minute allowed.", MAX_SPENDS_PER_MINUTE)
            );
        }
    }

}
