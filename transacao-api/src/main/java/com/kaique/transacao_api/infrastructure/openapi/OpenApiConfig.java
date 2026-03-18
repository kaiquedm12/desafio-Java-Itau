package com.kaique.transacao_api.infrastructure.openapi;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Transacao API",
                version = "v1",
                description = "API REST de transacoes/estatisticas e operacoes de usuarios (deposito/transferencia) com JWT.")
)
public class OpenApiConfig {
}

