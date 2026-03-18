package com.kaique.transacao_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.kaique.transacao_api.support.IntegrationTestBase;

class TransacaoEndpointsIT extends IntegrationTestBase {

    @Test
    void deveAdicionarTransacaoECalcularEstatistica() throws Exception {
        mockMvc.perform(post("/transacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "valor": 100.0,
                          "dataHora": "2026-03-16T14:59:30Z"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.sum").value(100.0));
    }

    @Test
    void deveLimparTransacoes() throws Exception {
        mockMvc.perform(post("/transacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "valor": 100.0,
                          "dataHora": "2026-03-16T14:59:30Z"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/transacao"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    void deveRetornar422QuandoTransacaoForInvalida() throws Exception {
        mockMvc.perform(post("/transacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "valor": null,
                          "dataHora": "2026-03-16T12:00:00Z"
                        }
                        """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void deveRetornar400QuandoJsonForInvalido() throws Exception {
        mockMvc.perform(post("/transacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"valor\": 10.0, \"dataHora\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

