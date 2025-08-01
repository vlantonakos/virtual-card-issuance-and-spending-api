package com.cardplatform.domain.model.transaction;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TransactionId implements Serializable {

    /**
     * This field represents the UUID value of the transaction identifier.
     */
    private UUID value;

    /**
     * Generates a new random TransactionId.
     *
     * @return a new TransactionId instance with a random UUID
     */
    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    /**
     * Creates a TransactionId from a UUID value.
     *
     * @param value the UUID value
     * @return a new TransactionId instance
     */
    public static TransactionId of(UUID value) {
        Objects.requireNonNull(value, "TransactionId value cannot be null");
        return new TransactionId(value);
    }

    /**
     * Creates a TransactionId from a String representation of UUID.
     *
     * @param value the String representation of UUID
     * @return a new TransactionId instance
     */
    public static TransactionId of(String value) {
        Objects.requireNonNull(value, "TransactionId value cannot be null");
        return new TransactionId(UUID.fromString(value));
    }

}
