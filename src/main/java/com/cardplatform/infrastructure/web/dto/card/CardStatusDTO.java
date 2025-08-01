package com.cardplatform.infrastructure.web.dto.card;

import com.cardplatform.domain.model.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardStatusDTO {

    /**
     * This field represents the unique identifier of the card.
     */
    private UUID cardId;

    /**
     * This field represents the current status of the card.
     */
    private CardStatus status;

    /**
     * This field represents the name of the cardholder.
     */
    private String cardholderName;

}