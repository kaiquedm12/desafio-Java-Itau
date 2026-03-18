package com.kaique.transacao_api.infrastructure.persistence.adapters;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kaique.transacao_api.domain.model.Transacao;
import com.kaique.transacao_api.domain.model.TransacaoStats;
import com.kaique.transacao_api.domain.model.TransacaoTipo;
import com.kaique.transacao_api.domain.ports.TransacaoRepository;
import com.kaique.transacao_api.infrastructure.persistence.jpa.entities.TransacaoJpaEntity;
import com.kaique.transacao_api.infrastructure.persistence.jpa.repositories.TransacaoJpaRepository;

@Component
public class TransacaoRepositoryJpaAdapter implements TransacaoRepository {

    private final TransacaoJpaRepository repository;
    private final Clock clock;

    public TransacaoRepositoryJpaAdapter(TransacaoJpaRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public Transacao save(Transacao transacao) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        TransacaoJpaEntity saved = repository.save(new TransacaoJpaEntity(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getUsuarioId(),
                transacao.getTransferenciaId(),
                transacao.getValor(),
                transacao.getDataHora(),
                now));
        return toDomain(saved);
    }

    @Transactional
    @Override
    public void deleteByTipo(TransacaoTipo tipo) {
        repository.deleteByTipo(tipo);
    }

    @Override
    public TransacaoStats calcularStats(TransacaoTipo tipo, OffsetDateTime inicioInclusivo, OffsetDateTime fimInclusivo) {
        TransacaoJpaRepository.StatsRow row = repository.calcularStats(tipo.name(), inicioInclusivo, fimInclusivo);
        return new TransacaoStats(
                row.getCount() == null ? 0 : row.getCount(),
                row.getSum(),
                row.getAvg(),
                row.getMin(),
                row.getMax());
    }

    @Override
    public List<Transacao> buscarHistoricoUsuario(UUID usuarioId, OffsetDateTime inicioInclusivo, OffsetDateTime fimInclusivo) {
        return repository.buscarHistoricoUsuario(usuarioId, inicioInclusivo, fimInclusivo).stream().map(TransacaoRepositoryJpaAdapter::toDomain)
                .toList();
    }

    private static Transacao toDomain(TransacaoJpaEntity entity) {
        return new Transacao(entity.getId(), entity.getTipo(), entity.getUsuarioId(), entity.getTransferenciaId(), entity.getValor(),
                entity.getDataHora());
    }
}

