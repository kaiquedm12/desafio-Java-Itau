package com.kaique.transacao_api.infrastructure.persistence.jpa.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kaique.transacao_api.infrastructure.persistence.jpa.entities.UsuarioJpaEntity;

import jakarta.persistence.LockModeType;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UsuarioJpaEntity u where u.id = :id")
    Optional<UsuarioJpaEntity> findByIdForUpdate(@Param("id") UUID id);
}

