package com.kaique.transacao_api.controller.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaRequestDTO(
        UUID origemId,
        UUID destinoId,
        BigDecimal valor
) {}

