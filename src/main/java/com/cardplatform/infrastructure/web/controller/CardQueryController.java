package com.cardplatform.infrastructure.web.controller;

import com.cardplatform.application.CardApplicationService;
import com.cardplatform.domain.exception.InvalidCardIdException;
import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.infrastructure.web.dto.card.CardBalanceDTO;
import com.cardplatform.infrastructure.web.dto.card.CardDTO;
import com.cardplatform.infrastructure.web.dto.card.CardStatusDTO;
import com.cardplatform.infrastructure.web.dto.transaction.TransactionHistoryResponseDTO;
import com.cardplatform.infrastructure.web.mapper.card.CardDTOMapper;
import com.cardplatform.infrastructure.web.mapper.transaction.TransactionHistoryDTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CardQueryController {

    /**
     * This controller handles queries related to card details, transaction history, balance, and status.
     * It provides endpoints to retrieve information about a specific card.
     */
    private final CardApplicationService cardApplicationService;

    /**
     * Mapper to convert domain object to DTO for API responses.
     */
    private final CardDTOMapper cardDTOMapper;

    /**
     * Mapper to convert domain object to DTO for API responses.
     */
    private final TransactionHistoryDTOMapper transactionHistoryDTOMapper;

    /**
     * Error message for invalid card ID format.
     */
    private static final String INVALID_CARD_ID_FORMAT = "Invalid card ID format: {}";

    /**
     * Retrieves the details of a specific card by its ID.
     *
     * @param cardId the unique identifier of the card
     * @return ResponseEntity containing CardDTO with card details
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<CardDTO> getCard(@PathVariable String cardId) {
        log.info("Query: Retrieving card details for ID: {}", cardId);
        try {
            Card card = cardApplicationService.getCard(CardId.of(cardId));
            CardDTO cardDTO = cardDTOMapper.mapTo(card);

            log.info("Query: Successfully retrieved card details for ID: {}", cardId);
            return ResponseEntity.ok(cardDTO);
        } catch (IllegalArgumentException e) {
            log.warn(INVALID_CARD_ID_FORMAT, cardId);
            throw new InvalidCardIdException(cardId, e);
        }
    }

    /**
     * Retrieves the transaction history for a specific card with pagination.
     *
     * @param cardId the unique identifier of the card
     * @param page the page number to retrieve (default is 0)
     * @param size the number of transactions per page (default is 20)
     * @return ResponseEntity containing TransactionHistoryResponseDTO with transaction history
     */
    @GetMapping("/{cardId}/transactions")
    public ResponseEntity<TransactionHistoryResponseDTO> getTransactionHistory(
            @PathVariable String cardId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Query: Retrieving transaction history for card: {}, page: {}, size: {}", cardId, page, size);

        try {

            Pageable pageable = PageRequest.of(page, size);
            Page<Transaction> transactionPage = cardApplicationService
                    .getTransactionHistory(CardId.of(cardId), pageable);

            TransactionHistoryResponseDTO response = transactionHistoryDTOMapper.mapTo(transactionPage);

            log.info("Query: Successfully retrieved {} transactions for card: {}",
                    response.getTransactions().size(), cardId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid card ID format: {}", cardId);
            throw new InvalidCardIdException(cardId, e);
        }
    }

    /**
     * Retrieves the balance of a specific card.
     *
     * @param cardId the unique identifier of the card
     * @return ResponseEntity containing CardBalanceDTO with card balance and status
     */
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<CardBalanceDTO> getCardBalance(@PathVariable String cardId) {
        log.info("Query: Retrieving balance for card: {}", cardId);
        try {
            Card card = cardApplicationService.getCard(CardId.of(cardId));

            CardBalanceDTO balanceDTO = CardBalanceDTO.builder()
                    .cardId(card.getId().getValue())
                    .balance(card.getBalance())
                    .status(card.getStatus())
                    .build();

            log.info("Query: Successfully retrieved balance for card: {}", cardId);
            return ResponseEntity.ok(balanceDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid card ID format: {}", cardId);
            throw new InvalidCardIdException(cardId, e);
        }
    }

    /**
     * Retrieves the status of a specific card.
     *
     * @param cardId the unique identifier of the card
     * @return ResponseEntity containing CardStatusDTO with card status and cardholder name
     */
    @GetMapping("/{cardId}/status")
    public ResponseEntity<CardStatusDTO> getCardStatus(@PathVariable String cardId) {
        log.info("Query: Retrieving status for card: {}", cardId);
        try {
            Card card = cardApplicationService.getCard(CardId.of(cardId));

            CardStatusDTO statusDTO = CardStatusDTO.builder()
                    .cardId(card.getId().getValue())
                    .status(card.getStatus())
                    .cardholderName(card.getCardholderName())
                    .build();

            log.info("Query: Successfully retrieved status for card: {}", cardId);
            return ResponseEntity.ok(statusDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid card ID format: {}", cardId);
            throw new InvalidCardIdException(cardId, e);
        }
    }

}