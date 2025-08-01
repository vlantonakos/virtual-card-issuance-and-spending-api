package com.cardplatform.application;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.domain.service.CardDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardApplicationService {

    private final CardDomainService cardDomainService;

    /**
     * Creates a new card.
     *
     * @param cardholderName the cardholder name
     * @param initialBalance the initial balance
     * @return the created card
     */
    public Card createCard(String cardholderName, BigDecimal initialBalance) {
        return cardDomainService.createCard(cardholderName, initialBalance);
    }

    /**
     * Processes a spend transaction.
     *
     * @param cardId the card identifier
     * @param amount the amount to spend
     * @return the updated card
     */
    public Card spendFromCard(CardId cardId, BigDecimal amount) {
        return cardDomainService.spendFromCard(cardId, amount);
    }

    /**
     * Processes a top-up transaction.
     *
     * @param cardId the card identifier
     * @param amount the amount to top up
     * @return the updated card
     */
    public Card topUpCard(CardId cardId, BigDecimal amount) {
        return cardDomainService.topUpCard(cardId, amount);
    }

    /**
     * Retrieves a card by its identifier.
     *
     * @param cardId the card identifier
     * @return the card
     */
    public Card getCard(CardId cardId) {
        return cardDomainService.getCard(cardId);
    }

    /**
     * Retrieves transaction history for a card.
     *
     * @param cardId the card identifier
     * @param pageable pagination information
     * @return page of transactions
     */
    public Page<Transaction> getTransactionHistory(CardId cardId, Pageable pageable) {
        return cardDomainService.getTransactionHistory(cardId, pageable);
    }

    /**
     * Blocks a card.
     *
     * @param cardId the card identifier
     * @return the updated card
     */
    public Card blockCard(CardId cardId) {
        return cardDomainService.blockCard(cardId);
    }

    /**
     * Activates a card.
     *
     * @param cardId the card identifier
     * @return the updated card
     */
    public Card activateCard(CardId cardId) {
        return cardDomainService.activateCard(cardId);
    }

}
