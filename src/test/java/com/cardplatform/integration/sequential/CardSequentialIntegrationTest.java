package com.cardplatform.integration.sequential;

import com.cardplatform.domain.model.enums.CardStatus;
import com.cardplatform.domain.model.enums.TransactionType;
import com.cardplatform.infrastructure.web.dto.card.CardBalanceDTO;
import com.cardplatform.infrastructure.web.dto.card.CardDTO;
import com.cardplatform.infrastructure.web.dto.card.CardStatusDTO;
import com.cardplatform.infrastructure.web.dto.card.CreateCardRequestDTO;
import com.cardplatform.infrastructure.web.dto.transaction.TransactionHistoryResponseDTO;
import com.cardplatform.infrastructure.web.dto.transaction.TransactionRequestDTO;
import com.cardplatform.infrastructure.web.exception.ErrorResponse;
import com.cardplatform.integration.BaseCardIntegrationTest;
import com.cardplatform.integration.manager.CardRequestManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CardSequentialIntegrationTest extends BaseCardIntegrationTest {

    private CardRequestManager requestManager;
    private ParameterizedTypeReference<CardDTO> cardTypeReference;
    private ParameterizedTypeReference<ErrorResponse> errorTypeReference;
    private ParameterizedTypeReference<TransactionHistoryResponseDTO> transactionHistoryTypeReference;
    private ParameterizedTypeReference<CardBalanceDTO> balanceTypeReference;
    private ParameterizedTypeReference<CardStatusDTO> statusTypeReference;

    private UUID cardId;

    @BeforeAll
    public final void setup() {
        this.setBaseUrl("http://localhost:" + this.getServerPort() + "/api/v1/cards");
        requestManager = new CardRequestManager(getBaseUrl(), getTestRestTemplate());
        cardTypeReference = new ParameterizedTypeReference<>() {};
        errorTypeReference = new ParameterizedTypeReference<>() {};
        transactionHistoryTypeReference = new ParameterizedTypeReference<>() {};
        balanceTypeReference = new ParameterizedTypeReference<>() {};
        statusTypeReference = new ParameterizedTypeReference<>() {};
    }

    /**
     * Tests the creation of a new card.
     *
     * Verifies that:
     * - A card can be successfully created
     * - The API returns CREATED status and a valid ID
     * - Initial balance is set correctly
     */
    @Test
    @Order(1)
    public void shouldCreateCard() {
        CreateCardRequestDTO createRequest = new CreateCardRequestDTO();
        createRequest.setCardholderName("Test User");
        createRequest.setInitialBalance(BigDecimal.valueOf(100.0));

        ResponseEntity<String> responseEntity = requestManager.makePostRequest(createRequest);
        cardId = getCreatedCardId(responseEntity);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(cardId);
    }

    /**
     * Tests the retrieval of the created card.
     *
     * Verifies that:
     * - The card can be retrieved successfully
     * - All card details are correct
     */
    @Test
    @Order(2)
    public void shouldGetCard() {
        ResponseEntity<CardDTO> responseEntity = requestManager.makeGetRequest(cardId, cardTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardDTO card = responseEntity.getBody();
        assertEquals(cardId, card.getId());
        assertEquals("Test User", card.getCardholderName());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(card.getBalance()));
        assertEquals(CardStatus.ACTIVE, card.getStatus());
    }

    /**
     * Tests spending from the card.
     *
     * Verifies that:
     * - Money can be spent from the card
     * - Balance is updated correctly
     */
    @Test
    @Order(3)
    public void shouldSpendFromCard() {
        TransactionRequestDTO spendRequest = new TransactionRequestDTO();
        spendRequest.setAmount(BigDecimal.valueOf(25.0));

        ResponseEntity<CardDTO> responseEntity = requestManager.makeTransactionRequest(
                cardId, spendRequest, "spend", cardTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardDTO card = responseEntity.getBody();
        assertEquals(0, BigDecimal.valueOf(75.0).compareTo(card.getBalance()));
    }

    /**
     * Tests topping up the card.
     *
     * Verifies that:
     * - Money can be added to the card
     * - Balance is updated correctly
     */
    @Test
    @Order(4)
    public void shouldTopUpCard() {
        TransactionRequestDTO topUpRequest = new TransactionRequestDTO();
        topUpRequest.setAmount(BigDecimal.valueOf(50.0));

        ResponseEntity<CardDTO> responseEntity = requestManager.makeTransactionRequest(
                cardId, topUpRequest, "topup", cardTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardDTO card = responseEntity.getBody();
        assertEquals(0, BigDecimal.valueOf(125.0).compareTo(card.getBalance()));
    }

    /**
     * Tests blocking the card.
     *
     * Verifies that:
     * - The card can be blocked
     * - Status is updated correctly
     */
    @Test
    @Order(5)
    public void shouldBlockCard() {
        ResponseEntity<CardDTO> responseEntity = requestManager.makeStatusUpdateRequest(
                cardId, "block", cardTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardDTO card = responseEntity.getBody();
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        assertEquals(0, BigDecimal.valueOf(125.0).compareTo(card.getBalance()));
    }

    /**
     * Tests activating the card.
     *
     * Verifies that:
     * - The card can be activated
     * - Status is updated correctly
     */
    @Test
    @Order(6)
    public void shouldActivateCard() {
        ResponseEntity<CardDTO> responseEntity = requestManager.makeStatusUpdateRequest(
                cardId, "activate", cardTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardDTO card = responseEntity.getBody();
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        assertEquals(0, BigDecimal.valueOf(125.0).compareTo(card.getBalance()));
    }

    /**
     * Tests spending from a non-existent card.
     *
     * Verifies that:
     * - Spending from non-existent card fails
     * - Appropriate error status is returned
     */
    @Test
    @Order(7)
    public void shouldFailToSpendFromNonExistentCard() {
        UUID nonExistentCardId = UUID.randomUUID();
        TransactionRequestDTO spendRequest = new TransactionRequestDTO();
        spendRequest.setAmount(BigDecimal.valueOf(10.0));

        ResponseEntity<ErrorResponse> responseEntity = requestManager.makeTransactionRequest(
                nonExistentCardId, spendRequest, "spend", errorTypeReference);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        ErrorResponse error = responseEntity.getBody();
        assertEquals("Business Rule Violation", error.getError());
        assertTrue(error.getMessage().contains("Card not found"));
    }

    /**
     * Tests spending more than the available balance.
     *
     * Verifies that:
     * - Spending more than balance fails
     * - Appropriate error status is returned
     */
    @Test
    @Order(8)
    public void shouldFailToSpendMoreThanBalance() {
        TransactionRequestDTO spendRequest = new TransactionRequestDTO();
        spendRequest.setAmount(BigDecimal.valueOf(200.0)); // More than current balance

        ResponseEntity<ErrorResponse> responseEntity = requestManager.makeTransactionRequest(
                cardId, spendRequest, "spend", errorTypeReference);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        ErrorResponse error = responseEntity.getBody();
        assertEquals("Business Rule Violation", error.getError());
        assertTrue(error.getMessage().contains("Insufficient balance"));
    }

    /**
     * Tests the final state of the card after all operations.
     *
     * Verifies that:
     * - The card is in the expected final state
     * - All previous operations were successful
     */
    @Test
    @Order(9)
    public void shouldGetCardAfterAllOperations() {
        ResponseEntity<CardDTO> responseEntity = requestManager.makeGetRequest(cardId, cardTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardDTO card = responseEntity.getBody();
        assertEquals(cardId, card.getId());
        assertEquals("Test User", card.getCardholderName());
        assertEquals(0, BigDecimal.valueOf(125.0).compareTo(card.getBalance()));
        assertEquals(CardStatus.ACTIVE, card.getStatus());
    }

    /**
     * Tests transaction history retrieval with pagination.
     */
    @Test
    @Order(10)
    public void shouldGetTransactionHistoryWithPagination() {
        ResponseEntity<TransactionHistoryResponseDTO> responseEntity =
                requestManager.makeGetRequestWithParams(cardId + "/transactions",
                        "page=0&size=2", transactionHistoryTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        TransactionHistoryResponseDTO response = responseEntity.getBody();
        assertNotNull(response.getTransactions());
        assertEquals(3, response.getTotalElements()); // initial topup + spend + topup
        assertEquals(2, response.getSize());
        assertEquals(0, response.getPage());
        assertTrue(response.getTotalPages() >= 2);

        // Verify we got exactly 2 transactions on this page
        assertEquals(2, response.getTransactions().size());
    }

    /**
     * Tests getting all transaction history without pagination limits.
     */
    @Test
    @Order(11)
    public void shouldGetAllTransactionHistory() {
        ResponseEntity<TransactionHistoryResponseDTO> responseEntity =
                requestManager.makeGetRequest(cardId + "/transactions", transactionHistoryTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        TransactionHistoryResponseDTO response = responseEntity.getBody();
        assertEquals(3, response.getTotalElements()); // initial topup + spend + topup
        assertEquals(20, response.getSize()); // default page size

        // Verify transaction types and amounts
        assertTrue(response.getTransactions().stream()
                .anyMatch(t -> TransactionType.TOPUP.equals(t.getType()) &&
                        BigDecimal.valueOf(100.0).compareTo(t.getAmount()) == 0));
        assertTrue(response.getTransactions().stream()
                .anyMatch(t -> TransactionType.SPEND.equals(t.getType()) &&
                        BigDecimal.valueOf(25.0).compareTo(t.getAmount()) == 0));
        assertTrue(response.getTransactions().stream()
                .anyMatch(t -> TransactionType.TOPUP.equals(t.getType()) &&
                        BigDecimal.valueOf(50.0).compareTo(t.getAmount()) == 0));
    }

    /**
     * Tests getting card balance endpoint.
     */
    @Test
    @Order(12)
    public void shouldGetCardBalance() {
        ResponseEntity<CardBalanceDTO> responseEntity =
                requestManager.makeGetRequest(cardId + "/balance", balanceTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardBalanceDTO balance = responseEntity.getBody();
        assertEquals(cardId, balance.getCardId());
        assertEquals(0, BigDecimal.valueOf(125.0).compareTo(balance.getBalance()));
        assertEquals(CardStatus.ACTIVE, balance.getStatus());
    }

    /**
     * Tests getting card status endpoint.
     */
    @Test
    @Order(13)
    public void shouldGetCardStatus() {
        ResponseEntity<CardStatusDTO> responseEntity =
                requestManager.makeGetRequest(cardId + "/status", statusTypeReference);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        CardStatusDTO status = responseEntity.getBody();
        assertEquals(cardId, status.getCardId());
        assertEquals(CardStatus.ACTIVE, status.getStatus());
        assertEquals("Test User", status.getCardholderName());
    }

    /**
     * Tests transaction history for non-existent card.
     */
    @Test
    @Order(14)
    public void shouldFailToGetTransactionHistoryForNonExistentCard() {
        UUID nonExistentCardId = UUID.randomUUID();

        ResponseEntity<ErrorResponse> responseEntity =
                requestManager.makeGetRequest(nonExistentCardId + "/transactions", errorTypeReference);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        ErrorResponse error = responseEntity.getBody();
        assertEquals("Business Rule Violation", error.getError());
        assertTrue(error.getMessage().contains("Card not found"));
    }

    /**
     * Tests getting balance for non-existent card.
     */
    @Test
    @Order(15)
    public void shouldFailToGetBalanceForNonExistentCard() {
        UUID nonExistentCardId = UUID.randomUUID();

        ResponseEntity<ErrorResponse> responseEntity =
                requestManager.makeGetRequest(nonExistentCardId + "/balance", errorTypeReference);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        ErrorResponse error = responseEntity.getBody();
        assertEquals("Card Not Found", error.getError());
    }

    /**
     * Tests getting status for non-existent card.
     */
    @Test
    @Order(16)
    public void shouldFailToGetStatusForNonExistentCard() {
        UUID nonExistentCardId = UUID.randomUUID();

        ResponseEntity<ErrorResponse> responseEntity =
                requestManager.makeGetRequest(nonExistentCardId + "/status", errorTypeReference);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        ErrorResponse error = responseEntity.getBody();
        assertEquals("Card Not Found", error.getError());
    }

    /**
     * Tests rate limiting - max 5 spends per minute per card.
     *
     * Verifies that:
     * - First 5 spend transactions succeed within the rate limit
     * - 6th spend transaction is rejected with rate limit error
     * - Error response contains appropriate rate limit message
     */
    @Test
    @Order(17)
    public void shouldEnforceRateLimitOnSpendTransactions() {
        // Create a new card with sufficient balance for rate limit testing
        CreateCardRequestDTO createRequest = new CreateCardRequestDTO();
        createRequest.setCardholderName("Rate Limit Test User");
        createRequest.setInitialBalance(BigDecimal.valueOf(1000.0));

        ResponseEntity<String> createResponse = requestManager.makePostRequest(createRequest);
        UUID rateLimitCardId = getCreatedCardId(createResponse);

        // Prepare spend request with small amount
        TransactionRequestDTO spendRequest = new TransactionRequestDTO();
        spendRequest.setAmount(BigDecimal.valueOf(10.0));

        // Execute 5 consecutive spend transactions (should all succeed)
        for (int i = 1; i <= 5; i++) {
            ResponseEntity<CardDTO> response = requestManager.makeTransactionRequest(
                    rateLimitCardId, spendRequest, "spend", cardTypeReference);

            assertEquals(HttpStatus.OK, response.getStatusCode(),
                    "Spend transaction " + i + " should succeed within rate limit");

            assertNotNull(response.getBody());
            assertEquals(CardStatus.ACTIVE, response.getBody().getStatus());

            // Verify balance decreases correctly
            BigDecimal expectedBalance = BigDecimal.valueOf(1000.0 - (i * 10.0));
            assertEquals(0, expectedBalance.compareTo(response.getBody().getBalance()),
                    "Balance should be " + expectedBalance + " after " + i + " transactions");
        }

        // 6th spend transaction should be rate limited
        ResponseEntity<ErrorResponse> rateLimitResponse = requestManager.makeTransactionRequest(
                rateLimitCardId, spendRequest, "spend", errorTypeReference);

        // Verify rate limit error response
        assertEquals(HttpStatus.BAD_REQUEST, rateLimitResponse.getStatusCode());
        assertNotNull(rateLimitResponse.getBody());

        ErrorResponse error = rateLimitResponse.getBody();
        assertEquals("Invalid Request", error.getError());
        assertEquals("Rate limit exceeded. Maximum 5 spends per minute allowed.", error.getMessage());
    }

    /**
     * Tests input validation for card creation.
     */
    @Test
    @Order(18)
    public void shouldValidateCardCreationInput() {
        // Test with invalid cardholder name (empty)
        CreateCardRequestDTO invalidRequest = new CreateCardRequestDTO();
        invalidRequest.setCardholderName("");
        invalidRequest.setInitialBalance(BigDecimal.valueOf(100.0));

        ResponseEntity<ErrorResponse> response = requestManager.makePostRequestError(invalidRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Failed", response.getBody().getError());
        assertNotNull(response.getBody().getValidationErrors());

        // Test with negative initial balance
        CreateCardRequestDTO negativeBalanceRequest = new CreateCardRequestDTO();
        negativeBalanceRequest.setCardholderName("Test User");
        negativeBalanceRequest.setInitialBalance(BigDecimal.valueOf(-10.0));

        ResponseEntity<ErrorResponse> negativeResponse = requestManager.makePostRequestError(negativeBalanceRequest);
        assertEquals(HttpStatus.BAD_REQUEST, negativeResponse.getStatusCode());
    }

    /**
     * Tests input validation for transactions.
     */
    @Test
    @Order(19)
    public void shouldValidateTransactionInput() {
        // Test with negative spend amount
        TransactionRequestDTO invalidSpend = new TransactionRequestDTO();
        invalidSpend.setAmount(BigDecimal.valueOf(-10.0));

        ResponseEntity<ErrorResponse> response = requestManager.makeTransactionRequest(
                cardId, invalidSpend, "spend", errorTypeReference);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Test with zero amount
        TransactionRequestDTO zeroAmount = new TransactionRequestDTO();
        zeroAmount.setAmount(BigDecimal.ZERO);

        ResponseEntity<ErrorResponse> zeroResponse = requestManager.makeTransactionRequest(
                cardId, zeroAmount, "spend", errorTypeReference);

        assertEquals(HttpStatus.BAD_REQUEST, zeroResponse.getStatusCode());
    }


    /**
     * Extracts card ID from create card response.
     *
     * @param responseEntity The response from card creation
     * @return The extracted card ID
     */
    private UUID getCreatedCardId(ResponseEntity<String> responseEntity) {
        try {
            String responseBody = responseEntity.getBody();
            if (responseBody != null) {
                return UUID.fromString(getObjectMapper().readTree(responseBody).get("id").asText());
            }
            throw new RuntimeException("Response body is null");
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract card ID from response: " + responseEntity.getBody(), e);
        }
    }

}