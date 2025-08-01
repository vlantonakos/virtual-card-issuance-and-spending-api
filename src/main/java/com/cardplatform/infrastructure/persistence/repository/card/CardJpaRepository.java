package com.cardplatform.infrastructure.persistence.repository.card;

import com.cardplatform.infrastructure.persistence.entity.card.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface CardJpaRepository extends JpaRepository<CardEntity, UUID> {

    /**
     * Find a card by id with pessimistic write lock for concurrent operations.
     *
     * @param id The card identifier.
     * @return An optional containing the card if found.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CardEntity c WHERE c.id = :id")
    Optional<CardEntity> findByIdWithLock(@Param("id") UUID id);

}
