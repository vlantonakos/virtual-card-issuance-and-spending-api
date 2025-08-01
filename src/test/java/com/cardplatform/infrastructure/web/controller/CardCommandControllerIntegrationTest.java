package com.cardplatform.infrastructure.web.controller;

import com.cardplatform.infrastructure.web.dto.card.CreateCardRequestDTO;
import com.cardplatform.infrastructure.web.dto.transaction.TransactionRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("qa")
@Transactional
class CardCommandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCardSuccessfully() throws Exception {
        // Given
        CreateCardRequestDTO request = CreateCardRequestDTO.builder()
                .cardholderName("John Doe")
                .initialBalance(new BigDecimal("100.00"))
                .build();

        // When & Then
        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardholderName").value("John Doe"))
                .andExpect(jsonPath("$.balance").value(100.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldFailToCreateCardWithInvalidData() throws Exception {
        // Given
        CreateCardRequestDTO request = CreateCardRequestDTO.builder()
                .cardholderName("")  // Invalid empty name
                .initialBalance(new BigDecimal("-10.00"))  // Invalid negative balance
                .build();

        // When & Then
        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    @Test
    void shouldProcessSpendTransactionSuccessfully() throws Exception {
        // Given - Create a card first
        CreateCardRequestDTO createRequest = CreateCardRequestDTO.builder()
                .cardholderName("Jane Smith")
                .initialBalance(new BigDecimal("200.00"))
                .build();

        MvcResult createResult = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String cardId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asText();

        // When - Spend from the card
        TransactionRequestDTO spendRequest = TransactionRequestDTO.builder()
                .amount(new BigDecimal("50.00"))
                .build();

        // Then
        mockMvc.perform(post("/cards/{cardId}/spend", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.00))
                .andExpect(jsonPath("$.id").value(cardId));
    }

    @Test
    void shouldFailSpendTransactionWithInsufficientBalance() throws Exception {
        // Given - Create a card with low balance
        CreateCardRequestDTO createRequest = CreateCardRequestDTO.builder()
                .cardholderName("Poor User")
                .initialBalance(new BigDecimal("10.00"))
                .build();

        MvcResult createResult = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String cardId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asText();

        // When - Try to spend more than balance
        TransactionRequestDTO spendRequest = TransactionRequestDTO.builder()
                .amount(new BigDecimal("50.00"))
                .build();

        // Then
        mockMvc.perform(post("/cards/{cardId}/spend", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spendRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Business Rule Violation"))
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }

}
