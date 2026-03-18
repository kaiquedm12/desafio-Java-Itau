package com.kaique.transacao_api.usecase.estatistica;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.domain.exceptions.BusinessRuleViolationException;
import com.kaique.transacao_api.domain.model.Transacao;
import com.kaique.transacao_api.domain.model.TransacaoStats;
import com.kaique.transacao_api.domain.model.TransacaoTipo;
import com.kaique.transacao_api.domain.ports.TransacaoRepository;
import com.kaique.transacao_api.usecase.config.AppProperties;
import com.kaique.transacao_api.usecase.validation.ValidationSupport;

@Service
public class TransacaoEstatisticaUseCase {

    private final TransacaoRepository transacaoRepository;
    private final Clock clock;
    private final AppProperties properties;

    public TransacaoEstatisticaUseCase(TransacaoRepository transacaoRepository, Clock clock, AppProperties properties) {
        this.transacaoRepository = transacaoRepository;
        this.clock = clock;
        this.properties = properties;
    }

    public void adicionar(Double valor, OffsetDateTime dataHora) {
        BigDecimal valorValidado = ValidationSupport.validarValorNaoNegativo(valor, "valor");
        if (dataHora == null) {
            throw new BusinessRuleViolationException("O campo dataHora e obrigatorio.");
        }
        OffsetDateTime agora = OffsetDateTime.now(clock);
        if (dataHora.isAfter(agora)) {
            throw new BusinessRuleViolationException("Data e hora da transacao nao podem ser no futuro.");
        }

        transacaoRepository.save(new Transacao(UUID.randomUUID(), TransacaoTipo.ESTATISTICA, null, null, valorValidado, dataHora));
    }

    public void limpar() {
        transacaoRepository.deleteByTipo(TransacaoTipo.ESTATISTICA);
    }

    public TransacaoStats obterStatsJanelaAtual() {
        OffsetDateTime agora = OffsetDateTime.now(clock);
        OffsetDateTime limite = agora.minusSeconds(properties.getStatistics().getWindowSeconds());
        return transacaoRepository.calcularStats(TransacaoTipo.ESTATISTICA, limite, agora);
    }
}

