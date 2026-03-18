package com.kaique.transacao_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kaique.transacao_api.controller.dtos.EstatisticaResponseDTO;
import com.kaique.transacao_api.controller.dtos.TransacaoRequestDTO;
import com.kaique.transacao_api.domain.model.TransacaoStats;
import com.kaique.transacao_api.usecase.estatistica.TransacaoEstatisticaUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Transacoes", description = "Endpoints do desafio (transacao e estatistica).")
public class TransacaoController {

    private final TransacaoEstatisticaUseCase useCase;

    public TransacaoController(TransacaoEstatisticaUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping(value = "/estatistica", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EstatisticaResponseDTO> estatisticas() {
        TransacaoStats stats = useCase.obterStatsJanelaAtual();
        if (stats.count() == 0) {
            return ResponseEntity.ok(new EstatisticaResponseDTO(0, 0, 0, 0, 0));
        }
        return ResponseEntity.ok(new EstatisticaResponseDTO(
                stats.count(),
                stats.sum().doubleValue(),
                stats.avg().doubleValue(),
                stats.min().doubleValue(),
                stats.max().doubleValue()));
    }

    @PostMapping(value = "/transacao", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> adicionarTransacao(@RequestBody TransacaoRequestDTO transacaoRequestDTO) {
        useCase.adicionar(transacaoRequestDTO.valor(), transacaoRequestDTO.dataHora());
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/transacao")
    public ResponseEntity<Void> limparTransacoes() {
        useCase.limpar();
        return ResponseEntity.ok().build();
    }
}
