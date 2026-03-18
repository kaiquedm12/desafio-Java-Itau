package com.kaique.transacao_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaique.transacao_api.controller.dtos.AuthLoginRequestDTO;
import com.kaique.transacao_api.controller.dtos.AuthLoginResponseDTO;
import com.kaique.transacao_api.usecase.auth.AuthenticateUserUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticacao e emissao de JWT.")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponseDTO> login(@RequestBody AuthLoginRequestDTO request) {
        AuthenticateUserUseCase.AuthResult result = authenticateUserUseCase.login(request.email(), request.senha());
        return ResponseEntity.ok(new AuthLoginResponseDTO(
                result.accessToken(),
                "Bearer",
                result.expiresInSeconds(),
                result.userId(),
                result.email()
        ));
    }
}
