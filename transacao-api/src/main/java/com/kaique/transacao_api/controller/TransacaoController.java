package com.kaique.transacao_api.controller;

import com.kaique.transacao_api.business.services.TransacaoService;
import com.kaique.transacao_api.controller.dtos.EstatisticaResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kaique.transacao_api.controller.dtos.TransacaoRequestDTO;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping("/estatistica")
    public ResponseEntity<EstatisticaResponseDTO> estatisticas() {
        return ResponseEntity.ok(transacaoService.obterEstatisticas());
    }

    @PostMapping("/transacao")
    public ResponseEntity<Void> adicionarTransacao(@RequestBody TransacaoRequestDTO transacaoRequestDTO) {
        transacaoService.adicionarTransacao(transacaoRequestDTO);   
        return ResponseEntity.status(201).build();
    }
}