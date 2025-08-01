package com.cardplatform.infrastructure.web.controller;

import com.cardplatform.application.CardApplicationService;
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

    private final CardApplicationService cardApplicationService;
    private final CardDTOMapper cardDTOMapper;
    private final TransactionHistoryDTOMapper transactionHistoryDTOMapper;

    /**
     * Retrieves card details by ID.
     *
     * @param cardId the card identifier
     * @return the card details
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<CardDTO> getCard(@PathVariable UUID cardId) {
        log.info("Query: Retrieving card details for ID: {}", cardId);

        Card card = cardApplicationService.getCard(CardId.of(cardId));
        CardDTO cardDTO = cardDTOMapper.mapTo(card);

        log.info("Query: Successfully retrieved card details for ID: {}", cardId);
        return ResponseEntity.ok(cardDTO);
    }

    /**
     * Retrieves transaction history for a card with pagination.
     *
     * @param cardId the card identifier
     * @param page the page number (0-based)
     * @param size the page size
     * @return the paginated transaction history
     */
    @GetMapping("/{cardId}/transactions")
    public ResponseEntity<TransactionHistoryResponseDTO> getTransactionHistory(
            @PathVariable UUID cardId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {

        log.info("Query: Retrieving transaction history for card: {}, page: {}, size: {}", cardId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = cardApplicationService.getTransactionHistory(
                CardId.of(cardId),
                pageable
        );

        TransactionHistoryResponseDTO response = transactionHistoryDTOMapper.mapTo(transactionPage);
        log.info("Query: Successfully retrieved {} transactions for card: {}",
                response.getTransactions().size(), cardId);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves card balance (lightweight endpoint for balance checks).
     *
     * @param cardId the card identifier
     * @return the card balance information
     */
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<CardBalanceDTO> getCardBalance(@PathVariable UUID cardId) {
        log.info("Query: Retrieving balance for card: {}", cardId);

        Card card = cardApplicationService.getCard(CardId.of(cardId));
        CardBalanceDTO balanceDTO = CardBalanceDTO.builder()
                .cardId(card.getId().getValue())
                .balance(card.getBalance())
                .status(card.getStatus())
                .build();

        log.info("Query: Successfully retrieved balance for card: {}", cardId);
        return ResponseEntity.ok(balanceDTO);
    }

    /**
     * Retrieves card status (lightweight endpoint for status checks).
     *
     * @param cardId the card identifier
     * @return the card status information
     */
    @GetMapping("/{cardId}/status")
    public ResponseEntity<CardStatusDTO> getCardStatus(@PathVariable UUID cardId) {
        log.info("Query: Retrieving status for card: {}", cardId);

        Card card = cardApplicationService.getCard(CardId.of(cardId));
        CardStatusDTO statusDTO = CardStatusDTO.builder()
                .cardId(card.getId().getValue())
                .status(card.getStatus())
                .cardholderName(card.getCardholderName())
                .build();

        log.info("Query: Successfully retrieved status for card: {}", cardId);
        return ResponseEntity.ok(statusDTO);
    }

}