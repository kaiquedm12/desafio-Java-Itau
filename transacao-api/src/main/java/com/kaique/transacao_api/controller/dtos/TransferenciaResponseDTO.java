package com.kaique.transacao_api.controller.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaResponseDTO(
        UUID origemId,
        UUID destinoId,
        BigDecimal valor,
        BigDecimal saldoOrigem,
        BigDecimal saldoDestino) {
}

