package com.cardplatform.infrastructure.web.mapper.card;

import com.cardplatform.domain.model.card.Card;
import com.cardplatform.infrastructure.web.dto.card.CardDTO;
import com.cardplatform.infrastructure.web.dto.card.CreateCardRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardDTOMapper {

    /**
     * This field represents an instance of the data transfer object mapper associated with the card.
     */
    CardDTOMapper INSTANCE = Mappers.getMapper(CardDTOMapper.class);

    /**
     * This method maps a Card domain object to a CardDTO object.
     *
     * @param card The card domain object.
     * @return A CardDTO object.
     */
    @Mapping(target = "id", source = "id.value")
    CardDTO mapTo(Card card);

    /**
     * This method maps a CardDTO object to a Card domain object.
     *
     * @param cardDTO The CardDTO object.
     * @return A Card domain object.
     */
    @Mapping(target = "id.value", source = "id")
    Card mapFrom(CardDTO cardDTO);

    /**
     * This method maps a CreateCardRequestDTO to domain parameters.
     * Note: This doesn't return a Card object as Card creation should go through domain factory methods.
     *
     * @param createCardRequestDTO The create card request DTO.
     * @return The cardholder name for domain service.
     */
    default String mapCardholderName(CreateCardRequestDTO createCardRequestDTO) {
        return createCardRequestDTO.getCardholderName();
    }

}
