package com.kaique.transacao_api.infrastructure.persistence.jpa.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.kaique.transacao_api.domain.model.TransacaoTipo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transacoes")
public class TransacaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 40)
    private TransacaoTipo tipo;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "transferencia_id")
    private UUID transferenciaId;

    @Column(name = "valor", nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Column(name = "data_hora", nullable = false)
    private OffsetDateTime dataHora;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected TransacaoJpaEntity() {
    }

    public TransacaoJpaEntity(UUID id, TransacaoTipo tipo, UUID usuarioId, UUID transferenciaId, BigDecimal valor, OffsetDateTime dataHora,
            OffsetDateTime createdAt) {
        this.id = id;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.transferenciaId = transferenciaId;
        this.valor = valor;
        this.dataHora = dataHora;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TransacaoTipo getTipo() {
        return tipo;
    }

    public void setTipo(TransacaoTipo tipo) {
        this.tipo = tipo;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getTransferenciaId() {
        return transferenciaId;
    }

    public void setTransferenciaId(UUID transferenciaId) {
        this.transferenciaId = transferenciaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(OffsetDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

