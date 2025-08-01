package com.cardplatform.infrastructure.web.mapper.transaction;

import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.infrastructure.web.dto.transaction.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionDTOMapper {

    /**
     * This field represents an instance of the data transfer object mapper associated with the transaction.
     */
    TransactionDTOMapper INSTANCE = Mappers.getMapper(TransactionDTOMapper.class);

    /**
     * This method maps a Transaction domain object to a TransactionDTO object.
     *
     * @param transaction The transaction domain object.
     * @return A TransactionDTO object.
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "cardId", source = "cardId.value")
    TransactionDTO mapTo(Transaction transaction);

    /**
     * This method maps a TransactionDTO object to a Transaction domain object.
     *
     * @param transactionDTO The TransactionDTO object.
     * @return A Transaction domain object.
     */
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "cardId.value", source = "cardId")
    Transaction mapFrom(TransactionDTO transactionDTO);

    /**
     * This method maps a list of Transaction domain objects to a list of TransactionDTO objects.
     *
     * @param transactions The list of transaction domain objects.
     * @return A list of TransactionDTO objects.
     */
    List<TransactionDTO> mapTo(List<Transaction> transactions);

    /**
     * This method maps a list of TransactionDTO objects to a list of Transaction domain objects.
     *
     * @param transactionDTOs The list of TransactionDTO objects.
     * @return A list of Transaction domain objects.
     */
    List<Transaction> mapFrom(List<TransactionDTO> transactionDTOs);

}
