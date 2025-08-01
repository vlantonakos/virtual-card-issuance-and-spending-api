package com.cardplatform.infrastructure.web.mapper.transaction;

import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.infrastructure.web.dto.transaction.TransactionHistoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionHistoryDTOMapper {

    private final TransactionDTOMapper transactionDTOMapper;

    /**
     * This method maps a Page of Transaction domain objects to a TransactionHistoryResponseDTO.
     *
     * @param transactionPage The page of transaction domain objects.
     * @return A TransactionHistoryResponseDTO object.
     */
    public TransactionHistoryResponseDTO mapTo(final Page<Transaction> transactionPage) {
        return TransactionHistoryResponseDTO.builder()
                .transactions(transactionDTOMapper.mapTo(transactionPage.getContent()))
                .page(transactionPage.getNumber())
                .size(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .first(transactionPage.isFirst())
                .last(transactionPage.isLast())
                .build();
    }

}
