package com.kaique.transacao_api.controller.dtos;

import java.math.BigDecimal;

public record DepositoRequestDTO(
        BigDecimal valor
) {}

