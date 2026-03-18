package com.kaique.transacao_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kaique.transacao_api.controller.dtos.TransferenciaRequestDTO;
import com.kaique.transacao_api.controller.dtos.TransferenciaResponseDTO;
import com.kaique.transacao_api.domain.exceptions.ForbiddenException;
import com.kaique.transacao_api.infrastructure.security.AuthenticatedUser;
import com.kaique.transacao_api.usecase.transfer.TransferUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Transferencias", description = "Transferencia de saldo entre usuarios (requer JWT).")
public class TransferenciaController {

    private final TransferUseCase transferUseCase;

    public TransferenciaController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @PostMapping(value = "/transferencias", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransferenciaResponseDTO> transferir(@RequestBody TransferenciaRequestDTO request) {
        ensureOriginIsSelf(request.origemId());
        TransferUseCase.TransferResult result = transferUseCase.execute(request.origemId(), request.destinoId(), request.valor());

        return ResponseEntity.status(201).body(new TransferenciaResponseDTO(
                request.origemId(),
                request.destinoId(),
                request.valor(),
                result.saldoOrigem(),
                result.saldoDestino()
        ));
    }

    private static void ensureOriginIsSelf(java.util.UUID origemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new ForbiddenException("Acesso negado.");
        }
        if (!origemId.equals(principal.userId())) {
            throw new ForbiddenException("Acesso negado.");
        }
    }
}
