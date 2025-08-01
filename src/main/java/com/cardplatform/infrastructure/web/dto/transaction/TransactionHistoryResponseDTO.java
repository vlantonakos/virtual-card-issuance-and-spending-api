package com.cardplatform.infrastructure.web.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionHistoryResponseDTO {

    /**
     * This field represents the list of transactions.
     */
    private List<TransactionDTO> transactions;

    /**
     * This field represents the current page number (0-based).
     */
    private int page;

    /**
     * This field represents the size of each page.
     */
    private int size;

    /**
     * This field represents the total number of elements.
     */
    private long totalElements;

    /**
     * This field represents the total number of pages.
     */
    private int totalPages;

    /**
     * This field represents whether this is the first page.
     */
    private boolean first;

    /**
     * This field represents whether this is the last page.
     */
    private boolean last;

}