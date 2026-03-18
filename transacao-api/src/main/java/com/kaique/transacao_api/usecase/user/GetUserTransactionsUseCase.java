package com.kaique.transacao_api.usecase.user;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.domain.model.Transacao;
import com.kaique.transacao_api.domain.ports.TransacaoRepository;
import com.kaique.transacao_api.usecase.validation.ValidationSupport;

@Service
public class GetUserTransactionsUseCase {

    private final TransacaoRepository transacaoRepository;
    private final Clock clock;

    public GetUserTransactionsUseCase(TransacaoRepository transacaoRepository, Clock clock) {
        this.transacaoRepository = transacaoRepository;
        this.clock = clock;
    }

    public List<Transacao> execute(UUID usuarioId, OffsetDateTime dataInicio, OffsetDateTime dataFim) {
        ValidationSupport.requireId(usuarioId, "id");

        OffsetDateTime inicio = dataInicio != null ? dataInicio : OffsetDateTime.parse("1970-01-01T00:00:00Z");
        OffsetDateTime fim = dataFim != null ? dataFim : OffsetDateTime.now(clock);
        return transacaoRepository.buscarHistoricoUsuario(usuarioId, inicio, fim);
    }
}

