package com.cardplatform.domain.service;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.domain.model.card.CardId;
import com.cardplatform.domain.model.enums.CardStatus;
import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.domain.port.card.CardRepository;
import com.cardplatform.domain.port.transaction.TransactionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CardDomainServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CardDomainService cardDomainService;

    private Card testCard;
    private CardId testCardId;

    @BeforeEach
    void setUp() {
        testCardId = CardId.generate();
        testCard = Card.create("John Doe", new BigDecimal("100.00"));
        testCard.setId(testCardId);
    }

    // Test 1: Card Creation - Core Requirement 
    @Test
    @Order(1)
    void shouldCreateCardSuccessfully_CoreRequirement() {
        // Given
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mock(Transaction.class));

        // When
        Card createdCard = cardDomainService.createCard("John Doe", new BigDecimal("100.00"));

        // Then
        assertNotNull(createdCard);
        assertEquals("John Doe", createdCard.getCardholderName());
        assertEquals(new BigDecimal("100.00"), createdCard.getBalance());
        assertEquals(CardStatus.ACTIVE, createdCard.getStatus());
        assertEquals(0L, createdCard.getVersion());

        // Verify interactions
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // Test 2: Successful Spend Transaction - Core Business Rule 
    @Test
    @Order(2)
    void shouldProcessSpendTransactionSuccessfully_BusinessRule() {
        // Given
        when(cardRepository.findByIdWithLock(testCardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mock(Transaction.class));
        when(transactionRepository.countByCardIdAndCreatedAtBetween(any(), anyLong(), anyLong())).thenReturn(0L);

        // When
        Card updatedCard = cardDomainService.spendFromCard(testCardId, new BigDecimal("30.00"));

        // Then
        assertNotNull(updatedCard);
        assertEquals(new BigDecimal("70.00"), updatedCard.getBalance()); // 100 - 30
        assertEquals(CardStatus.ACTIVE, updatedCard.getStatus());

        // Verify pessimistic locking was used
        verify(cardRepository, times(1)).findByIdWithLock(testCardId);
        verify(cardRepository, times(1)).save(testCard);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // Test 3: Insufficient Balance Prevention - Critical Business Rule 
    @Test
    @Order(3)
    void shouldPreventOverspending_CriticalBusinessRule() {
        // Given
        Card lowBalanceCard = Card.create("Poor User", new BigDecimal("10.00"));
        lowBalanceCard.setId(testCardId);

        when(cardRepository.findByIdWithLock(testCardId)).thenReturn(Optional.of(lowBalanceCard));
        when(transactionRepository.countByCardIdAndCreatedAtBetween(any(), anyLong(), anyLong())).thenReturn(0L);

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cardDomainService.spendFromCard(testCardId, new BigDecimal("50.00"))
        );

        assertEquals("Insufficient balance", exception.getMessage());

        // Verify no save occurred (transaction should be rolled back)
        verify(cardRepository, never()).save(any(Card.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // Test 4: Top-up Transaction - Core Functionality 
    @Test
    @Order(4)
    void shouldProcessTopUpTransactionSuccessfully_CoreFunctionality() {
        // Given
        when(cardRepository.findByIdWithLock(testCardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mock(Transaction.class));

        // When
        Card updatedCard = cardDomainService.topUpCard(testCardId, new BigDecimal("50.00"));

        // Then
        assertNotNull(updatedCard);
        assertEquals(new BigDecimal("150.00"), updatedCard.getBalance()); // 100 + 50
        assertEquals(CardStatus.ACTIVE, updatedCard.getStatus());

        verify(cardRepository, times(1)).findByIdWithLock(testCardId);
        verify(cardRepository, times(1)).save(testCard);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // Test 5: Rate Limiting - Bonus Feature 
    @Test
    @Order(5)
    void shouldEnforceRateLimit_BonusFeature() {
        // Given - Rate limit exceeded (5 recent transactions)
        when(transactionRepository.countByCardIdAndCreatedAtBetween(any(), anyLong(), anyLong())).thenReturn(5L);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cardDomainService.spendFromCard(testCardId, new BigDecimal("10.00"))
        );

        assertTrue(exception.getMessage().contains("Rate limit exceeded"));
        assertTrue(exception.getMessage().contains("Maximum 5 spends per minute"));

        // Verify rate limiting prevented any database operations
        verify(cardRepository, never()).findByIdWithLock(any());
        verify(cardRepository, never()).save(any(Card.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // Test 6: Card Blocking - Bonus Feature 
    @Test
    @Order(6)
    void shouldBlockCardSuccessfully_BonusFeature() {
        // Given
        when(cardRepository.findByIdWithLock(testCardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // When
        Card blockedCard = cardDomainService.blockCard(testCardId);

        // Then
        assertNotNull(blockedCard);
        assertEquals(CardStatus.BLOCKED, blockedCard.getStatus());

        verify(cardRepository, times(1)).findByIdWithLock(testCardId);
        verify(cardRepository, times(1)).save(testCard);
    }

    // Test 7: Spending on Blocked Card Prevention - Business Rule 
    @Test
    @Order(7)
    void shouldPreventSpendingOnBlockedCard_BusinessRule() {
        // Given
        Card blockedCard = Card.create("Blocked User", new BigDecimal("100.00"));
        blockedCard.setId(testCardId);
        blockedCard.block(); // Block the card

        when(cardRepository.findByIdWithLock(testCardId)).thenReturn(Optional.of(blockedCard));
        when(transactionRepository.countByCardIdAndCreatedAtBetween(any(), anyLong(), anyLong())).thenReturn(0L);

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cardDomainService.spendFromCard(testCardId, new BigDecimal("10.00"))
        );

        assertEquals("Card is not active", exception.getMessage());

        // Verify no transaction occurred
        verify(cardRepository, never()).save(any(Card.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // Test 8: Card Not Found - Business Rule 
    @Test
    @Order(8)
    void shouldHandleNonExistentCard_BusinessRule() {
        // Given
        when(cardRepository.findByIdWithLock(testCardId)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cardDomainService.spendFromCard(testCardId, new BigDecimal("10.00"))
        );

        assertTrue(exception.getMessage().contains("Card not found"));
    }

}