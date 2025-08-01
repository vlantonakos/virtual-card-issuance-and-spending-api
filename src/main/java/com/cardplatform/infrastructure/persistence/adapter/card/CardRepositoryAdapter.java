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

    private final CardJpaRepository cardJpaRepository;
    private final CardEntityMapper cardEntityMapper;

    @Override
    public Card save(Card card) {
        log.debug("Saving card with ID: {}", card.getId());

        CardEntity cardEntity = cardEntityMapper.mapToEntity(card);
        CardEntity savedEntity = cardJpaRepository.save(cardEntity);
        Card savedCard = cardEntityMapper.mapToDomain(savedEntity);

        log.debug("Successfully saved card with ID: {}", savedCard.getId());
        return savedCard;
    }

    @Override
    public Optional<Card> findById(CardId cardId) {
        log.debug("Finding card by ID: {}", cardId);

        return cardJpaRepository.findById(cardId.getValue())
                .map(cardEntityMapper::mapToDomain);
    }

    @Override
    public Optional<Card> findByIdWithLock(CardId cardId) {
        log.debug("Finding card by ID with lock: {}", cardId);

        return cardJpaRepository.findByIdWithLock(cardId.getValue())
                .map(cardEntityMapper::mapToDomain);
    }

    @Override
    public boolean existsById(CardId cardId) {
        log.debug("Checking if card exists by ID: {}", cardId);

        return cardJpaRepository.existsById(cardId.getValue());
    }

}