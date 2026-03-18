package com.kaique.transacao_api.infrastructure.persistence.jpa.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transferencias")
public class TransferenciaJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "origem_id", nullable = false)
    private UUID origemId;

    @Column(name = "destino_id", nullable = false)
    private UUID destinoId;

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_hora", nullable = false)
    private OffsetDateTime dataHora;

    protected TransferenciaJpaEntity() {
    }

    public TransferenciaJpaEntity(UUID id, UUID origemId, UUID destinoId, BigDecimal valor, OffsetDateTime dataHora) {
        this.id = id;
        this.origemId = origemId;
        this.destinoId = destinoId;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrigemId() {
        return origemId;
    }

    public void setOrigemId(UUID origemId) {
        this.origemId = origemId;
    }

    public UUID getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(UUID destinoId) {
        this.destinoId = destinoId;
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
}

