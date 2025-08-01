package com.cardplatform.infrastructure.web.dto.card;

import com.cardplatform.domain.model.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDTO {

    /**
     * This field represents the unique identifier of the card.
     */
    private UUID id;

    /**
     * This field represents the name of the cardholder.
     */
    @NotBlank(message = "Cardholder name cannot be blank")
    private String cardholderName;

    /**
     * This field represents the current balance of the card.
     */
    @NotNull(message = "Balance cannot be null")
    @PositiveOrZero(message = "Balance must be positive or zero")
    private BigDecimal balance;

    /**
     * This field represents the timestamp when the card was created.
     */
    private Instant createdAt;

    /**
     * This field represents the current status of the card.
     */
    private CardStatus status;

    /**
     * This field represents the version for optimistic locking.
     */
    private Long version;

}
