package com.kaique.transacao_api.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kaique.transacao_api.business.models.Usuario;
import com.kaique.transacao_api.business.services.UsuarioService;
import com.kaique.transacao_api.controller.dtos.DepositoRequestDTO;
import com.kaique.transacao_api.controller.dtos.SaldoResponseDTO;
import com.kaique.transacao_api.controller.dtos.UsuarioCreateRequestDTO;
import com.kaique.transacao_api.controller.dtos.UsuarioResponseDTO;

@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping(value = "/usuarios", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioCreateRequestDTO request) {
        Usuario usuario = usuarioService.criarUsuario(request.nome(), request.email());
        return ResponseEntity.status(201).body(toResponse(usuario));
    }

    @GetMapping(value = "/usuarios/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable UUID id) {
        Usuario usuario = usuarioService.buscarUsuario(id);
        return ResponseEntity.ok(toResponse(usuario));
    }

    @GetMapping(value = "/usuarios/{id}/saldo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaldoResponseDTO> consultarSaldo(@PathVariable UUID id) {
        return ResponseEntity.ok(new SaldoResponseDTO(usuarioService.consultarSaldo(id)));
    }

    @PostMapping(value = "/usuarios/{id}/deposito", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaldoResponseDTO> depositar(@PathVariable UUID id, @RequestBody DepositoRequestDTO request) {
        return ResponseEntity.ok(new SaldoResponseDTO(usuarioService.depositar(id, request.valor())));
    }

    private static UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getSaldo());
    }
}

