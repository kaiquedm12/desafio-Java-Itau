package com.kaique.transacao_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kaique.transacao_api.business.services.UsuarioService;
import com.kaique.transacao_api.controller.dtos.TransferenciaRequestDTO;
import com.kaique.transacao_api.controller.dtos.TransferenciaResponseDTO;

@RestController
public class TransferenciaController {

    private final UsuarioService usuarioService;

    public TransferenciaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping(value = "/transferencias", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransferenciaResponseDTO> transferir(@RequestBody TransferenciaRequestDTO request) {
        UsuarioService.TransferenciaResult result = usuarioService.transferir(request.origemId(), request.destinoId(),
                request.valor());

        return ResponseEntity.status(201).body(new TransferenciaResponseDTO(
                request.origemId(),
                request.destinoId(),
                request.valor(),
                result.saldoOrigem(),
                result.saldoDestino()
        ));
    }
}

