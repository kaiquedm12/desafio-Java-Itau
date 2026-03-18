package com.kaique.transacao_api.domain.model;

import java.math.BigDecimal;

public record TransacaoStats(
        long count,
        BigDecimal sum,
        BigDecimal avg,
        BigDecimal min,
        BigDecimal max
) {
}

