package com.kaique.transacao_api.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaique.transacao_api.controller.dtos.DepositoRequestDTO;
import com.kaique.transacao_api.controller.dtos.SaldoResponseDTO;
import com.kaique.transacao_api.controller.dtos.TransacaoHistoricoItemDTO;
import com.kaique.transacao_api.controller.dtos.UsuarioCreateRequestDTO;
import com.kaique.transacao_api.controller.dtos.UsuarioResponseDTO;
import com.kaique.transacao_api.domain.exceptions.ForbiddenException;
import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.infrastructure.security.AuthenticatedUser;
import com.kaique.transacao_api.usecase.user.CreateUserUseCase;
import com.kaique.transacao_api.usecase.user.DepositUseCase;
import com.kaique.transacao_api.usecase.user.GetBalanceUseCase;
import com.kaique.transacao_api.usecase.user.GetUserTransactionsUseCase;
import com.kaique.transacao_api.usecase.user.GetUserUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Usuarios", description = "Cadastro de usuarios, saldo, deposito e historico.")
public class UsuarioController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final DepositUseCase depositUseCase;
    private final GetUserTransactionsUseCase getUserTransactionsUseCase;

    public UsuarioController(
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase,
            GetBalanceUseCase getBalanceUseCase,
            DepositUseCase depositUseCase,
            GetUserTransactionsUseCase getUserTransactionsUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.getBalanceUseCase = getBalanceUseCase;
        this.depositUseCase = depositUseCase;
        this.getUserTransactionsUseCase = getUserTransactionsUseCase;
    }

    @PostMapping(value = "/usuarios", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioCreateRequestDTO request) {
        CreateUserUseCase.CreateUserResult result = createUserUseCase.execute(request.nome(), request.email(), request.senha());
        return ResponseEntity.status(201).body(toResponse(result.usuario(), result.senhaTemporaria()));
    }

    @GetMapping(value = "/usuarios/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable UUID id) {
        Usuario usuario = getUserUseCase.execute(id);
        return ResponseEntity.ok(toResponse(usuario, null));
    }

    @GetMapping(value = "/usuarios/{id}/saldo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaldoResponseDTO> consultarSaldo(@PathVariable UUID id) {
        ensureSelf(id);
        return ResponseEntity.ok(new SaldoResponseDTO(getBalanceUseCase.execute(id)));
    }

    @PostMapping(value = "/usuarios/{id}/deposito", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaldoResponseDTO> depositar(@PathVariable UUID id, @RequestBody DepositoRequestDTO request) {
        ensureSelf(id);
        return ResponseEntity.ok(new SaldoResponseDTO(depositUseCase.execute(id, request.valor())));
    }

    @GetMapping(value = "/usuarios/{id}/transacoes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<TransacaoHistoricoItemDTO>> historico(
            @PathVariable UUID id,
            @RequestParam(value = "dataInicio", required = false) java.time.OffsetDateTime dataInicio,
            @RequestParam(value = "dataFim", required = false) java.time.OffsetDateTime dataFim) {
        ensureSelf(id);
        return ResponseEntity.ok(getUserTransactionsUseCase.execute(id, dataInicio, dataFim).stream()
                .map(t -> new TransacaoHistoricoItemDTO(t.getId(), t.getTipo(), t.getValor(), t.getDataHora(), t.getTransferenciaId()))
                .toList());
    }

    private static UsuarioResponseDTO toResponse(Usuario usuario, String senhaTemporaria) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getSaldo(), senhaTemporaria);
    }

    private void ensureSelf(UUID userId) {
        UUID authenticatedId = getAuthenticatedUserId();
        if (!userId.equals(authenticatedId)) {
            throw new ForbiddenException("Acesso negado.");
        }
    }

    private static UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ForbiddenException("Acesso negado.");
        }
        if (auth.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal.userId();
        }
        throw new ForbiddenException("Acesso negado.");
    }
}
