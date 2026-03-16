package com.kaique.transacao_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kaique.transacao_api.business.services.TransacaoService;
import com.kaique.transacao_api.controller.dtos.EstatisticaResponseDTO;
import com.kaique.transacao_api.controller.dtos.TransacaoRequestDTO;

@RestController
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping(value = "/estatistica", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EstatisticaResponseDTO> estatisticas() {
        return ResponseEntity.ok(transacaoService.obterEstatisticas());
    }

    @PostMapping(value = "/transacao", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> adicionarTransacao(@RequestBody TransacaoRequestDTO transacaoRequestDTO) {
        transacaoService.adicionarTransacao(transacaoRequestDTO);   
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/transacao")
    public ResponseEntity<Void> limparTransacoes() {
        transacaoService.limparTransacoes();
        return ResponseEntity.ok().build();
    }
}