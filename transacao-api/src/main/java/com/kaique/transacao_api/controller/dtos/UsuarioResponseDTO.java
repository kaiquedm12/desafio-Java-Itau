package com.kaique.transacao_api.controller.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email,
        BigDecimal saldo
) {}

