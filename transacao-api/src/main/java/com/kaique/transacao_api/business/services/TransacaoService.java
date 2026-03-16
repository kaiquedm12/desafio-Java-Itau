package com.kaique.transacao_api.business.services;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kaique.transacao_api.controller.dtos.EstatisticaResponseDTO;
import com.kaique.transacao_api.controller.dtos.TransacaoRequestDTO;
import com.kaique.transacao_api.infrastructure.exceptions.UnprocessableEntity;
@Service
public class TransacaoService {

    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    private final Collection<TransacaoRequestDTO> listaTransacoes = new ConcurrentLinkedQueue<>();
    private final Clock clock;
    private final long statisticsWindowSeconds;

    public TransacaoService(
            Clock clock,
            @Value("${app.statistics.window-seconds:60}") long statisticsWindowSeconds) {
        this.clock = clock;
        this.statisticsWindowSeconds = statisticsWindowSeconds;
    }

    public void adicionarTransacao(TransacaoRequestDTO transacaoRequestDTO) {
        log.info("Adicionando transação: {}", transacaoRequestDTO);
        validarTransacao(transacaoRequestDTO);
        removerTransacoesExpiradas();

        listaTransacoes.add(transacaoRequestDTO);
    }

    public void limparTransacoes() {
        log.info("Limpando todas as transações");
        listaTransacoes.clear();
    }

    public DoubleSummaryStatistics estatisticaTransacoes() {
        log.info("Calculando estatísticas das transações");
        OffsetDateTime agora = agora();
        OffsetDateTime limite = agora.minusSeconds(statisticsWindowSeconds);

        removerTransacoesExpiradas();

        return listaTransacoes.stream()
            .filter(t -> !t.dataHora().isBefore(limite))
            .filter(t -> !t.dataHora().isAfter(agora))
            .mapToDouble(TransacaoRequestDTO::valor)
            .summaryStatistics();
    }

    public EstatisticaResponseDTO obterEstatisticas() {
        log.info("Obtendo estatísticas das transações");
        DoubleSummaryStatistics stats = estatisticaTransacoes();

        if (stats.getCount() == 0) {
            return new EstatisticaResponseDTO(0, 0, 0, 0, 0);
        }

        return new EstatisticaResponseDTO(
                stats.getCount(),
                stats.getSum(),
                stats.getAverage(),
                stats.getMin(),
                stats.getMax()
        );
    }

    private void validarTransacao(TransacaoRequestDTO transacaoRequestDTO) {
        if (transacaoRequestDTO == null) {
            throw new UnprocessableEntity("A transação deve ser informada.");
        }

        if (transacaoRequestDTO.valor() == null) {
            throw new UnprocessableEntity("O campo valor é obrigatório.");
        }

        if (transacaoRequestDTO.dataHora() == null) {
            throw new UnprocessableEntity("O campo dataHora é obrigatório.");
        }

        if (transacaoRequestDTO.dataHora().isAfter(agora())) {
            log.error("Data e hora da transação estão no futuro: {}", transacaoRequestDTO.dataHora());
            throw new UnprocessableEntity("Data e hora da transação não podem ser no futuro.");
        }

        if (transacaoRequestDTO.valor() < 0) {
            log.error("Valor da transação é negativo: {}", transacaoRequestDTO.valor());
            throw new UnprocessableEntity("Valor da transação deve ser maior ou igual a zero.");
        }
    }

    private void removerTransacoesExpiradas() {
        OffsetDateTime limite = agora().minusSeconds(statisticsWindowSeconds);
        listaTransacoes.removeIf(transacao -> transacao.dataHora() != null && transacao.dataHora().isBefore(limite));
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.now(clock);
    }

}