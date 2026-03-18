package com.kaique.transacao_api.domain.ports;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.kaique.transacao_api.domain.model.Transacao;
import com.kaique.transacao_api.domain.model.TransacaoStats;
import com.kaique.transacao_api.domain.model.TransacaoTipo;

public interface TransacaoRepository {
    Transacao save(Transacao transacao);

    void deleteByTipo(TransacaoTipo tipo);

    TransacaoStats calcularStats(TransacaoTipo tipo, OffsetDateTime inicioInclusivo, OffsetDateTime fimInclusivo);

    List<Transacao> buscarHistoricoUsuario(UUID usuarioId, OffsetDateTime inicioInclusivo, OffsetDateTime fimInclusivo);
}

