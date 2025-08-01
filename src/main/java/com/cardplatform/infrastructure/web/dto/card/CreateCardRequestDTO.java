package com.cardplatform.infrastructure.web.dto.card;

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

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateCardRequestDTO {

    /**
     * This field represents the name of the cardholder.
     */
    @NotBlank(message = "Cardholder name cannot be blank")
    private String cardholderName;

    /**
     * This field represents the initial balance of the card.
     */
    @NotNull(message = "Initial balance cannot be null")
    @PositiveOrZero(message = "Initial balance must be positive or zero")
    private BigDecimal initialBalance;

}
