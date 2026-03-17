package com.kaique.transacao_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaique.transacao_api.business.services.UsuarioService;
import com.kaique.transacao_api.controller.dtos.SaldoResponseDTO;
import com.kaique.transacao_api.controller.dtos.TransferenciaResponseDTO;
import com.kaique.transacao_api.controller.dtos.UsuarioResponseDTO;
import com.kaique.transacao_api.infrastructure.exceptions.ApiExceptionHandler;

class UsuarioTransferenciaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UsuarioService usuarioService = new UsuarioService();
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UsuarioController(usuarioService), new TransferenciaController(usuarioService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void deveCriarUsuarioComSaldoZero() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "Kaique",
                          "email": "kaique@example.com"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Kaique"))
                .andExpect(jsonPath("$.email").value("kaique@example.com"))
                .andExpect(jsonPath("$.saldo").value(0.0));
    }

    @Test
    void deveRetornar422QuandoEmailForDuplicado() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "A",
                          "email": "dup@example.com"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "B",
                          "email": "DUP@example.com"
                        }
                        """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveDepositarEConsultarSaldo() throws Exception {
        UsuarioResponseDTO usuario = criarUsuario("Origem", "origem@example.com");

        mockMvc.perform(post("/usuarios/%s/deposito".formatted(usuario.id()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "valor": 10.00 }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(10.0));

        mockMvc.perform(get("/usuarios/%s/saldo".formatted(usuario.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(10.0));
    }

    @Test
    void deveTransferirEntreUsuarios() throws Exception {
        UsuarioResponseDTO origem = criarUsuario("Origem", "origem2@example.com");
        UsuarioResponseDTO destino = criarUsuario("Destino", "destino2@example.com");

        mockMvc.perform(post("/usuarios/%s/deposito".formatted(origem.id()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "valor": 20.00 }
                        """))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/transferencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "origemId": "%s",
                          "destinoId": "%s",
                          "valor": 7.50
                        }
                        """.formatted(origem.id(), destino.id())))
                .andExpect(status().isCreated())
                .andReturn();

        TransferenciaResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                TransferenciaResponseDTO.class);

        // Confere saldos retornados e persistidos
        org.junit.jupiter.api.Assertions.assertAll(
                () -> org.junit.jupiter.api.Assertions.assertEquals(12.50, response.saldoOrigem().doubleValue()),
                () -> org.junit.jupiter.api.Assertions.assertEquals(7.50, response.saldoDestino().doubleValue())
        );

        SaldoResponseDTO saldoOrigem = buscarSaldo(origem.id());
        SaldoResponseDTO saldoDestino = buscarSaldo(destino.id());

        org.junit.jupiter.api.Assertions.assertAll(
                () -> org.junit.jupiter.api.Assertions.assertEquals(12.50, saldoOrigem.saldo().doubleValue()),
                () -> org.junit.jupiter.api.Assertions.assertEquals(7.50, saldoDestino.saldo().doubleValue())
        );
    }

    @Test
    void deveRetornar422QuandoSaldoForInsuficiente() throws Exception {
        UsuarioResponseDTO origem = criarUsuario("Origem", "origem3@example.com");
        UsuarioResponseDTO destino = criarUsuario("Destino", "destino3@example.com");

        mockMvc.perform(post("/transferencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "origemId": "%s",
                          "destinoId": "%s",
                          "valor": 1.00
                        }
                        """.formatted(origem.id(), destino.id())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExistir() throws Exception {
        mockMvc.perform(get("/usuarios/%s".formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400QuandoJsonForInvalido() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\": \"X\", \"email\":"))
                .andExpect(status().isBadRequest());
    }

    private UsuarioResponseDTO criarUsuario(String nome, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "%s",
                          "email": "%s"
                        }
                        """.formatted(nome, email)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), UsuarioResponseDTO.class);
    }

    private SaldoResponseDTO buscarSaldo(UUID id) throws Exception {
        MvcResult result = mockMvc.perform(get("/usuarios/%s/saldo".formatted(id)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), SaldoResponseDTO.class);
    }
}

