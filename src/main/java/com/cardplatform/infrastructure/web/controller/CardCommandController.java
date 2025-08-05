package com.cardplatform.infrastructure.web.controller;

import com.cardplatform.application.CardApplicationService;
import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.infrastructure.web.dto.card.CardDTO;
import com.cardplatform.infrastructure.web.dto.card.CreateCardRequestDTO;
import com.cardplatform.infrastructure.web.dto.transaction.TransactionRequestDTO;
import com.cardplatform.infrastructure.web.mapper.card.CardDTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CardCommandController {

    /**
     * Application service coordinating card-related use cases and orchestrating domain services.
     */
    private final CardApplicationService cardApplicationService;

    /**
     * Mapper for converting between Card domain objects and CardDTO data transfer objects.
     */
    private final CardDTOMapper cardDTOMapper;

    /**
     * Creates a new virtual card.
     *
     * @param createCardRequest the card creation request
     * @return the created card
     */
    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CreateCardRequestDTO createCardRequest) {
        log.info("Command: Creating new card for cardholder: {}", createCardRequest.getCardholderName());

        Card createdCard = cardApplicationService.createCard(
                createCardRequest.getCardholderName(),
                createCardRequest.getInitialBalance()
        );

        CardDTO cardDTO = cardDTOMapper.mapTo(createdCard);
        log.info("Command: Successfully created card with ID: {}", cardDTO.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(cardDTO);
    }

    /**
     * Processes a spend transaction on a card.
     *
     * @param cardId the card identifier
     * @param spendRequest the spend request
     * @return the updated card
     */
    @PostMapping("/{cardId}/spend")
    public ResponseEntity<CardDTO> spendFromCard(@PathVariable UUID cardId,
                                                 @Valid @RequestBody TransactionRequestDTO spendRequest) {

        log.info("Command: Processing spend transaction for card: {}, amount: {}", cardId, spendRequest.getAmount());

        Card updatedCard = cardApplicationService.spendFromCard(
                CardId.of(cardId),
                spendRequest.getAmount()
        );

        CardDTO cardDTO = cardDTOMapper.mapTo(updatedCard);
        log.info("Command: Successfully processed spend transaction for card: {}", cardId);

        return ResponseEntity.ok(cardDTO);
    }

    /**
     * Processes a top-up transaction on a card.
     *
     * @param cardId the card identifier
     * @param topUpRequest the top-up request
     * @return the updated card
     */
    @PostMapping("/{cardId}/topup")
    public ResponseEntity<CardDTO> topUpCard(@PathVariable UUID cardId,
                                             @Valid @RequestBody TransactionRequestDTO topUpRequest) {

        log.info("Command: Processing top-up transaction for card: {}, amount: {}", cardId, topUpRequest.getAmount());

        Card updatedCard = cardApplicationService.topUpCard(
                CardId.of(cardId),
                topUpRequest.getAmount()
        );

        CardDTO cardDTO = cardDTOMapper.mapTo(updatedCard);
        log.info("Command: Successfully processed top-up transaction for card: {}", cardId);

        return ResponseEntity.ok(cardDTO);
    }

    /**
     * Updates card status to BLOCKED.
     *
     * @param cardId the card identifier
     * @return the updated card
     */
    @PutMapping("/{cardId}/block")
    public ResponseEntity<CardDTO> blockCard(@PathVariable UUID cardId) {
        log.info("Command: Blocking card: {}", cardId);

        Card updatedCard = cardApplicationService.blockCard(CardId.of(cardId));
        CardDTO cardDTO = cardDTOMapper.mapTo(updatedCard);

        log.info("Command: Successfully blocked card: {}", cardId);
        return ResponseEntity.ok(cardDTO);
    }

    /**
     * Updates card status to ACTIVE.
     *
     * @param cardId the card identifier
     * @return the updated card
     */
    @PutMapping("/{cardId}/activate")
    public ResponseEntity<CardDTO> activateCard(@PathVariable UUID cardId) {
        log.info("Command: Activating card: {}", cardId);

        Card updatedCard = cardApplicationService.activateCard(CardId.of(cardId));
        CardDTO cardDTO = cardDTOMapper.mapTo(updatedCard);

        log.info("Command: Successfully activated card: {}", cardId);
        return ResponseEntity.ok(cardDTO);
    }

}
