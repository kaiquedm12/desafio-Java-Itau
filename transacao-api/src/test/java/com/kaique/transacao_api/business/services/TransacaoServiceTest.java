package com.kaique.transacao_api.business.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.kaique.transacao_api.controller.dtos.EstatisticaResponseDTO;
import com.kaique.transacao_api.controller.dtos.TransacaoRequestDTO;
import com.kaique.transacao_api.infrastructure.exceptions.UnprocessableEntity;

class TransacaoServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-03-16T15:00:00Z"), ZoneOffset.UTC);
    private final TransacaoService service = new TransacaoService(clock, 60);

    @Test
    void deveAdicionarTransacaoValida() {
        TransacaoRequestDTO transacao = new TransacaoRequestDTO(100.0, OffsetDateTime.now(clock));

        service.adicionarTransacao(transacao);

        EstatisticaResponseDTO stats = service.obterEstatisticas();

        assertAll(
                () -> assertEquals(1, stats.count()),
                () -> assertEquals(100.0, stats.sum()),
                () -> assertEquals(100.0, stats.avg()),
                () -> assertEquals(100.0, stats.min()),
                () -> assertEquals(100.0, stats.max())
        );
    }

    @Test
    void deveLancarErroQuandoDataForFutura() {
        TransacaoRequestDTO transacao = new TransacaoRequestDTO(50.0, OffsetDateTime.now(clock).plusSeconds(10));

        assertThrows(UnprocessableEntity.class, () -> service.adicionarTransacao(transacao));
    }

    @Test
    void deveLancarErroQuandoValorForNegativo() {
        TransacaoRequestDTO transacao = new TransacaoRequestDTO(-10.0, OffsetDateTime.now(clock));

        assertThrows(UnprocessableEntity.class, () -> service.adicionarTransacao(transacao));
    }

    @Test
    void deveLancarErroQuandoValorNaoForInformado() {
        TransacaoRequestDTO transacao = new TransacaoRequestDTO(null, OffsetDateTime.now(clock));

        assertThrows(UnprocessableEntity.class, () -> service.adicionarTransacao(transacao));
    }

    @Test
    void deveLancarErroQuandoDataHoraNaoForInformada() {
        TransacaoRequestDTO transacao = new TransacaoRequestDTO(10.0, null);

        assertThrows(UnprocessableEntity.class, () -> service.adicionarTransacao(transacao));
    }

    @Test
    void deveRetornarZerosQuandoNaoExistiremTransacoes() {
        EstatisticaResponseDTO stats = service.obterEstatisticas();

        assertAll(
                () -> assertEquals(0, stats.count()),
                () -> assertEquals(0, stats.sum()),
                () -> assertEquals(0, stats.avg()),
                () -> assertEquals(0, stats.min()),
                () -> assertEquals(0, stats.max())
        );
    }

    @Test
    void deveConsiderarApenasTransacoesDosUltimosSessentaSegundos() {
        service.adicionarTransacao(new TransacaoRequestDTO(50.0, OffsetDateTime.now(clock).minusSeconds(60)));
        service.adicionarTransacao(new TransacaoRequestDTO(100.0, OffsetDateTime.now(clock).minusSeconds(10)));
        service.adicionarTransacao(new TransacaoRequestDTO(200.0, OffsetDateTime.now(clock).minusSeconds(61)));

        EstatisticaResponseDTO stats = service.obterEstatisticas();

        assertAll(
                () -> assertEquals(2, stats.count()),
                () -> assertEquals(150.0, stats.sum()),
                () -> assertEquals(75.0, stats.avg()),
                () -> assertEquals(50.0, stats.min()),
                () -> assertEquals(100.0, stats.max())
        );
    }

    @Test
    void deveLimparTransacoes() {
        service.adicionarTransacao(new TransacaoRequestDTO(100.0, OffsetDateTime.now(clock)));

        service.limparTransacoes();

        EstatisticaResponseDTO stats = service.obterEstatisticas();

        assertEquals(0, stats.count());
    }
}
