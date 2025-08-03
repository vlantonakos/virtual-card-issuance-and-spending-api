package com.cardplatform.domain.model.card;

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
     * Handles both formats: with and without hyphens.
     *
     * @param value the String representation of UUID
     * @return a new CardId instance
     * @throws IllegalArgumentException if the string is not a valid UUID format
     */
    public static CardId of(String value) {
        Objects.requireNonNull(value, "CardId value cannot be null");
        try {
            String normalizedValue = value.trim().toLowerCase();
            if (normalizedValue.length() == 32 && !normalizedValue.contains("-")) {
                normalizedValue = addHyphensToUuid(normalizedValue);
            }
            return new CardId(UUID.fromString(normalizedValue));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid card ID format: " + value, e);
        }
    }

    /**
     * Adds hyphens to a 32-character UUID string to make it valid.
     * Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     */
    private static String addHyphensToUuid(String uuid32) {
        if (uuid32.length() != 32) {
            throw new IllegalArgumentException("UUID without hyphens must be exactly 32 characters");
        }
        return uuid32.substring(0, 8) + "-" +
                uuid32.substring(8, 12) + "-" +
                uuid32.substring(12, 16) + "-" +
                uuid32.substring(16, 20) + "-" +
                uuid32.substring(20);
    }

}
