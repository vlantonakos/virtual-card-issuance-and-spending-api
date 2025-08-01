package com.cardplatform.infrastructure.web.dto.transaction;

import com.cardplatform.domain.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {

    /**
     * This field represents the unique identifier of the transaction.
     */
    private UUID id;

    /**
     * This field represents the card identifier associated with this transaction.
     */
    @NotNull(message = "Card ID cannot be null")
    private UUID cardId;

    /**
     * This field represents the type of transaction.
     */
    private TransactionType type;

    /**
     * This field represents the amount of the transaction.
     */
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    /**
     * This field represents the timestamp when the transaction was created.
     */
    private Instant createdAt;

}
