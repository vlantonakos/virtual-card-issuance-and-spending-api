package com.cardplatform.infrastructure.persistence.adapter.card;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.domain.port.card.CardRepository;
import com.cardplatform.infrastructure.persistence.entity.card.CardEntity;
import com.cardplatform.infrastructure.persistence.repository.card.CardJpaRepository;
import com.cardplatform.infrastructure.web.mapper.card.CardEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CardRepositoryAdapter implements CardRepository {

    /**
     * JPA repository interface for performing CRUD operations on Card entities.
     */
    private final CardJpaRepository cardJpaRepository;

    /**
     * Mapper for converting between {@link Card} domain objects and {@link CardEntity} persistence entities.
     */
    private final CardEntityMapper cardEntityMapper;

    /**
     * Saves the given {@link Card} to the database.
     * Converts the domain {@code Card} to an entity, saves it, then maps it back.
     *
     * @param card the card to save
     * @return the saved card with any updated state (e.g., generated IDs)
     */
    @Override
    public Card save(Card card) {
        log.debug("Saving card with ID: {}", card.getId());

        CardEntity cardEntity = cardEntityMapper.mapToEntity(card);
        CardEntity savedEntity = cardJpaRepository.save(cardEntity);
        Card savedCard = cardEntityMapper.mapToDomain(savedEntity);

        log.debug("Successfully saved card with ID: {}", savedCard.getId());
        return savedCard;
    }

    /**
     * Finds a card by its {@link CardId}.
     *
     * @param cardId the unique identifier of the card
     * @return an {@link Optional} containing the found {@code Card} or empty if not found
     */
    @Override
    public Optional<Card> findById(CardId cardId) {
        log.debug("Finding card by ID: {}", cardId);

        return cardJpaRepository.findById(cardId.getValue())
                .map(cardEntityMapper::mapToDomain);
    }

    /**
     * Checks if a card exists in the database by its {@link CardId}.
     *
     * @param cardId the unique identifier of the card to check
     * @return {@code true} if a card with the given ID exists, {@code false} otherwise
     */
    @Override
    public boolean existsById(CardId cardId) {
        log.debug("Checking if card exists by ID: {}", cardId);

        return cardJpaRepository.existsById(cardId.getValue());
    }

}