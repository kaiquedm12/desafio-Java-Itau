package com.kaique.transacao_api.infrastructure.persistence.adapters;

import org.springframework.stereotype.Component;

import com.kaique.transacao_api.domain.model.Transferencia;
import com.kaique.transacao_api.domain.ports.TransferenciaRepository;
import com.kaique.transacao_api.infrastructure.persistence.jpa.entities.TransferenciaJpaEntity;
import com.kaique.transacao_api.infrastructure.persistence.jpa.repositories.TransferenciaJpaRepository;

@Component
public class TransferenciaRepositoryJpaAdapter implements TransferenciaRepository {

    private final TransferenciaJpaRepository repository;

    public TransferenciaRepositoryJpaAdapter(TransferenciaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transferencia save(Transferencia transferencia) {
        TransferenciaJpaEntity saved = repository.save(new TransferenciaJpaEntity(
                transferencia.getId(),
                transferencia.getOrigemId(),
                transferencia.getDestinoId(),
                transferencia.getValor(),
                transferencia.getDataHora()));
        return new Transferencia(saved.getId(), saved.getOrigemId(), saved.getDestinoId(), saved.getValor(), saved.getDataHora());
    }
}

