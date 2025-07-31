package com.cardplatform.domain.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CardId implements Serializable {

    /**
     * This field represents the UUID value of the card identifier.
     */
    private UUID value;

    /**
     * Generates a new random CardId.
     *
     * @return a new CardId instance with a random UUID
     */
    public static CardId generate() {
        return new CardId(UUID.randomUUID());
    }

    /**
     * Creates a CardId from a UUID value.
     *
     * @param value the UUID value
     * @return a new CardId instance
     */
    public static CardId of(UUID value) {
        Objects.requireNonNull(value, "CardId value cannot be null");
        return new CardId(value);
    }

    /**
     * Creates a CardId from a String representation of UUID.
     *
     * @param value the String representation of UUID
     * @return a new CardId instance
     */
    public static CardId of(String value) {
        Objects.requireNonNull(value, "CardId value cannot be null");
        return new CardId(UUID.fromString(value));
    }

}
