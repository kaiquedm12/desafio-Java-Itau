package com.kaique.transacao_api.usecase.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.domain.model.Usuario;

@Service
public class GetBalanceUseCase {

    private final GetUserUseCase getUserUseCase;

    public GetBalanceUseCase(GetUserUseCase getUserUseCase) {
        this.getUserUseCase = getUserUseCase;
    }

    public BigDecimal execute(UUID id) {
        Usuario usuario = getUserUseCase.execute(id);
        return usuario.getSaldo();
    }
}

