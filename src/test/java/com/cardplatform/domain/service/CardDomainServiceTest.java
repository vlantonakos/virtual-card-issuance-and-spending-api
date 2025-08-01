package com.cardplatform.domain.service;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.domain.port.card.CardRepository;
import com.cardplatform.domain.port.transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardDomainServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private CardDomainService cardDomainService;

    @BeforeEach
    void setUp() {
        cardDomainService = new CardDomainService(cardRepository, transactionRepository);
    }

    @Test
    void shouldCreateCardSuccessfully() {
        // Given
        String cardholderName = "John Doe";
        BigDecimal initialBalance = new BigDecimal("100.00");
        Card expectedCard = Card.create(cardholderName, initialBalance);

        when(cardRepository.save(any(Card.class))).thenReturn(expectedCard);

        // When
        Card result = cardDomainService.createCard(cardholderName, initialBalance);

        // Then
        assertNotNull(result);
        assertEquals(cardholderName, result.getCardholderName());
        assertEquals(initialBalance, result.getBalance());
        verify(cardRepository).save(any(Card.class));
        verify(transactionRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSpendingFromNonExistentCard() {
        // Given
        CardId cardId = CardId.generate();
        BigDecimal amount = new BigDecimal("50.00");

        when(cardRepository.findByIdWithLock(cardId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalStateException.class,
                () -> cardDomainService.spendFromCard(cardId, amount));
    }

}