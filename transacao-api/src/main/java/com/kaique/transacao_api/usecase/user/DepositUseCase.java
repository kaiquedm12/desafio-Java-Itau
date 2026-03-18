package com.kaique.transacao_api.usecase.user;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaique.transacao_api.domain.exceptions.NotFoundException;
import com.kaique.transacao_api.domain.model.Transacao;
import com.kaique.transacao_api.domain.model.TransacaoTipo;
import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.domain.ports.TransacaoRepository;
import com.kaique.transacao_api.domain.ports.UsuarioRepository;
import com.kaique.transacao_api.usecase.validation.ValidationSupport;

@Service
public class DepositUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;
    private final Clock clock;

    public DepositUseCase(UsuarioRepository usuarioRepository, TransacaoRepository transacaoRepository, Clock clock) {
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
        this.clock = clock;
    }

    @Transactional
    public BigDecimal execute(UUID usuarioId, BigDecimal valor) {
        ValidationSupport.requireId(usuarioId, "id");
        BigDecimal valorValidado = ValidationSupport.validarValorMonetario(valor, "valor");
        Usuario usuario = usuarioRepository.findByIdForUpdate(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));

        usuario.creditar(valorValidado);
        Usuario salvo = usuarioRepository.save(usuario);

        OffsetDateTime agora = OffsetDateTime.now(clock);
        transacaoRepository.save(new Transacao(UUID.randomUUID(), TransacaoTipo.DEPOSITO, usuarioId, null, valorValidado, agora));
        return salvo.getSaldo();
    }
}

