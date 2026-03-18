package com.kaique.transacao_api.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transacao {

    private final UUID id;
    private final TransacaoTipo tipo;
    private final UUID usuarioId; // nullable for ESTATISTICA
    private final UUID transferenciaId; // nullable
    private final BigDecimal valor;
    private final OffsetDateTime dataHora;

    public Transacao(
            UUID id,
            TransacaoTipo tipo,
            UUID usuarioId,
            UUID transferenciaId,
            BigDecimal valor,
            OffsetDateTime dataHora) {
        this.id = Objects.requireNonNull(id, "id");
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.usuarioId = usuarioId;
        this.transferenciaId = transferenciaId;
        this.valor = Objects.requireNonNull(valor, "valor");
        this.dataHora = Objects.requireNonNull(dataHora, "dataHora");
    }

    public UUID getId() {
        return id;
    }

    public TransacaoTipo getTipo() {
        return tipo;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getTransferenciaId() {
        return transferenciaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }
}

