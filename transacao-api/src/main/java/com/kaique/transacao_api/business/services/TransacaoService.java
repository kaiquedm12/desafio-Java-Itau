package com.kaique.transacao_api.business.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.controller.dtos.EstatisticaResponseDTO;
import com.kaique.transacao_api.controller.dtos.TransacaoRequestDTO;
import com.kaique.transacao_api.infrastructure.exceptions.UnprocessableEntity;

@Service
public class TransacaoService {

    private final List<TransacaoRequestDTO> listaTransacoes = new ArrayList<>();

    public void adicionarTransacao(TransacaoRequestDTO transacaoRequestDTO) {
        if (transacaoRequestDTO.dataHora().isAfter(OffsetDateTime.now())){
            throw new UnprocessableEntity("Data e hora da transação não podem ser no futuro.");
        }
        if (transacaoRequestDTO.valor() < 0) {
            throw new UnprocessableEntity("Valor da transação deve ser maior ou igual a zero.");
        }

        listaTransacoes.add(transacaoRequestDTO);
    }

    public void limparTransacoes() {
        listaTransacoes.clear();
    }

    public DoubleSummaryStatistics estatisticaTransacoes() {
        OffsetDateTime limite = OffsetDateTime.now().minusSeconds(60);

        return listaTransacoes.stream()
                .filter(t -> t.dataHora().isAfter(limite))
                .mapToDouble(TransacaoRequestDTO::valor)
                .summaryStatistics();
    }

    public EstatisticaResponseDTO obterEstatisticas() {
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

}