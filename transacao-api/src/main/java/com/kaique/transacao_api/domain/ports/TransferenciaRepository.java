package com.kaique.transacao_api.domain.ports;

import com.kaique.transacao_api.domain.model.Transferencia;

public interface TransferenciaRepository {
    Transferencia save(Transferencia transferencia);
}

