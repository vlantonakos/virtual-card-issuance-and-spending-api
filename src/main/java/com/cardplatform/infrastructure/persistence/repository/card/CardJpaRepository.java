package com.cardplatform.infrastructure.persistence.repository.card;

import com.cardplatform.infrastructure.persistence.entity.card.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CardJpaRepository extends JpaRepository<CardEntity, UUID> {
}
