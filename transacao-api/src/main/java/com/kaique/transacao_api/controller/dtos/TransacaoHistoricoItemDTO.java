package com.kaique.transacao_api.controller.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.kaique.transacao_api.domain.model.TransacaoTipo;

public record TransacaoHistoricoItemDTO(
        UUID id,
        TransacaoTipo tipo,
        BigDecimal valor,
        OffsetDateTime dataHora,
        UUID transferenciaId
) {
}

