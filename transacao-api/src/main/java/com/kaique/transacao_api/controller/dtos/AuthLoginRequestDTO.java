package com.kaique.transacao_api.controller.dtos;

public record AuthLoginRequestDTO(
        String email,
        String senha
) {
}

