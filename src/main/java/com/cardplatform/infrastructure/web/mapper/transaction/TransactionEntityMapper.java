package com.cardplatform.infrastructure.web.mapper.transaction;

import com.cardplatform.domain.model.transaction.Transaction;
import com.cardplatform.infrastructure.persistence.entity.transaction.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionEntityMapper {

    /**
     * This field represents an instance of the entity mapper associated with the transaction.
     */
    TransactionEntityMapper INSTANCE = Mappers.getMapper(TransactionEntityMapper.class);

    /**
     * This method maps a Transaction domain object to a TransactionEntity.
     *
     * @param transaction The transaction domain object.
     * @return A TransactionEntity object.
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "card", ignore = true)
    TransactionEntity mapToEntity(Transaction transaction);

    /**
     * This method maps a TransactionEntity to a Transaction domain object.
     *
     * @param transactionEntity The TransactionEntity object.
     * @return A Transaction domain object.
     */
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "cardId.value", source = "card.id")
    Transaction mapToDomain(TransactionEntity transactionEntity);

    /**
     * This method maps a list of TransactionEntity objects to a list of Transaction domain objects.
     *
     * @param transactionEntities The list of TransactionEntity objects.
     * @return A list of Transaction domain objects.
     */
    List<Transaction> mapToDomain(List<TransactionEntity> transactionEntities);

}
