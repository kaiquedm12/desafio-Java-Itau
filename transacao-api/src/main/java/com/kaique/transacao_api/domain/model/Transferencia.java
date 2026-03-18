package com.kaique.transacao_api.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transferencia {

    private final UUID id;
    private final UUID origemId;
    private final UUID destinoId;
    private final BigDecimal valor;
    private final OffsetDateTime dataHora;

    public Transferencia(UUID id, UUID origemId, UUID destinoId, BigDecimal valor, OffsetDateTime dataHora) {
        this.id = Objects.requireNonNull(id, "id");
        this.origemId = Objects.requireNonNull(origemId, "origemId");
        this.destinoId = Objects.requireNonNull(destinoId, "destinoId");
        this.valor = Objects.requireNonNull(valor, "valor");
        this.dataHora = Objects.requireNonNull(dataHora, "dataHora");
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrigemId() {
        return origemId;
    }

    public UUID getDestinoId() {
        return destinoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }
}

