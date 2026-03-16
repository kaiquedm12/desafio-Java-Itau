package com.kaique.transacao_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kaique.transacao_api.business.services.TransacaoService;
import com.kaique.transacao_api.controller.dtos.TransacaoRequestDTO;
import com.kaique.transacao_api.infrastructure.exceptions.ApiExceptionHandler;

class TransacaoControllerTest {

    private MockMvc mockMvc;

        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                Clock clock = Clock.fixed(Instant.parse("2026-03-16T15:00:00Z"), ZoneOffset.UTC);
                TransacaoService service = new TransacaoService(clock, 60);

                objectMapper = new ObjectMapper()
                                .registerModule(new JavaTimeModule())
                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                mockMvc = MockMvcBuilders
                                .standaloneSetup(new TransacaoController(service))
                                .setControllerAdvice(new ApiExceptionHandler())
                                .build();
        }

    @Test
    void deveAdicionarTransacao() throws Exception {
                TransacaoRequestDTO dto = new TransacaoRequestDTO(100.0, OffsetDateTime.parse("2026-03-16T14:59:30Z"));

        mockMvc.perform(post("/transacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
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
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRetornar400QuandoJsonForInvalido() throws Exception {
        mockMvc.perform(post("/transacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"valor\": 10.0, \"dataHora\":"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarEstatisticas() throws Exception {
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
                .andExpect(jsonPath("$.sum").value(100));
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
}
