package com.cardplatform.infrastructure.web.mapper.card;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.infrastructure.persistence.entity.card.CardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardEntityMapper {

    /**
     * This field represents an instance of the entity mapper associated with the card.
     */
    CardEntityMapper INSTANCE = Mappers.getMapper(CardEntityMapper.class);

    /**
     * This method maps a Card domain object to a CardEntity.
     *
     * @param card The card domain object.
     * @return A CardEntity object.
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "transactions", ignore = true)
    CardEntity mapToEntity(Card card);

    /**
     * This method maps a CardEntity to a Card domain object.
     *
     * @param cardEntity The CardEntity object.
     * @return A Card domain object.
     */
    @Mapping(target = "id.value", source = "id")
    Card mapToDomain(CardEntity cardEntity);

}