package com.kaique.transacao_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaique.transacao_api.support.IntegrationTestBase;

@SuppressWarnings("SameParameterValue")
class AuthAndUserFlowIT extends IntegrationTestBase {

    @org.springframework.beans.factory.annotation.Autowired
    ObjectMapper objectMapper;

    @Test
    void deveAutenticarEDepositarTransferirEConsultarHistorico() throws Exception {
        UUID origemId = criarUsuario("Origem", "origem@example.com", "senha-origem");
        UUID destinoId = criarUsuario("Destino", "destino@example.com", "senha-destino");

        String tokenOrigem = login("origem@example.com", "senha-origem");

        // sem token -> 401
        mockMvc.perform(post("/usuarios/%s/deposito".formatted(origemId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\": 10.00}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/usuarios/%s/deposito".formatted(origemId))
                        .header("Authorization", "Bearer " + tokenOrigem)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\": 20.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(20.0));

        mockMvc.perform(get("/usuarios/%s/saldo".formatted(origemId))
                        .header("Authorization", "Bearer " + tokenOrigem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(20.0));

        mockMvc.perform(post("/transferencias")
                        .header("Authorization", "Bearer " + tokenOrigem)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "origemId": "%s",
                                  "destinoId": "%s",
                                  "valor": 7.50
                                }
                                """.formatted(origemId, destinoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldoOrigem").value(12.5))
                .andExpect(jsonPath("$.saldoDestino").value(7.5));

        mockMvc.perform(get("/usuarios/%s/transacoes".formatted(origemId))
                        .header("Authorization", "Bearer " + tokenOrigem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());

        // token do destino nao pode transferir como origem
        String tokenDestino = login("destino@example.com", "senha-destino");
        mockMvc.perform(post("/transferencias")
                        .header("Authorization", "Bearer " + tokenDestino)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "origemId": "%s",
                                  "destinoId": "%s",
                                  "valor": 1.00
                                }
                                """.formatted(origemId, destinoId)))
                .andExpect(status().isForbidden());
    }

    private UUID criarUsuario(String nome, String email, String senha) throws Exception {
        MvcResult result = mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "%s",
                                  "email": "%s",
                                  "senha": "%s"
                                }
                                """.formatted(nome, email, senha)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsByteArray());
        return UUID.fromString(node.get("id").asText());
    }

    private String login(String email, String senha) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "senha": "%s"
                                }
                                """.formatted(email, senha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode node = objectMapper.readTree(json);
        return node.get("accessToken").asText();
    }
}

