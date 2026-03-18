package com.kaique.transacao_api.infrastructure.persistence.jpa.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaique.transacao_api.infrastructure.persistence.jpa.entities.TransferenciaJpaEntity;

public interface TransferenciaJpaRepository extends JpaRepository<TransferenciaJpaEntity, UUID> {
}

