package com.cardplatform.domain.port.card;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;

import java.util.Optional;

public interface CardRepository {

    /**
     * Saves a card entity.
     *
     * @param card the card to save
     * @return the saved card
     */
    Card save(Card card);

    /**
     * Finds a card by its identifier.
     *
     * @param cardId the card identifier
     * @return an optional containing the card if found
     */
    Optional<Card> findById(CardId cardId);

    /**
     * Checks if a card exists by its identifier.
     *
     * @param cardId the card identifier
     * @return true if the card exists, false otherwise
     */
    boolean existsById(CardId cardId);

}