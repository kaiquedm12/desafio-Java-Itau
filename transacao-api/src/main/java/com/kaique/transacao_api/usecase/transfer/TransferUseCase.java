package com.kaique.transacao_api.usecase.transfer;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaique.transacao_api.domain.exceptions.NotFoundException;
import com.kaique.transacao_api.domain.model.Transacao;
import com.kaique.transacao_api.domain.model.TransacaoTipo;
import com.kaique.transacao_api.domain.model.Transferencia;
import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.domain.ports.TransacaoRepository;
import com.kaique.transacao_api.domain.ports.TransferenciaRepository;
import com.kaique.transacao_api.domain.ports.UsuarioRepository;
import com.kaique.transacao_api.usecase.validation.ValidationSupport;

@Service
public class TransferUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final TransacaoRepository transacaoRepository;
    private final Clock clock;

    public TransferUseCase(
            UsuarioRepository usuarioRepository,
            TransferenciaRepository transferenciaRepository,
            TransacaoRepository transacaoRepository,
            Clock clock) {
        this.usuarioRepository = usuarioRepository;
        this.transferenciaRepository = transferenciaRepository;
        this.transacaoRepository = transacaoRepository;
        this.clock = clock;
    }

    @Transactional
    public TransferResult execute(UUID origemId, UUID destinoId, BigDecimal valor) {
        ValidationSupport.requireId(origemId, "origemId");
        ValidationSupport.requireId(destinoId, "destinoId");
        ValidationSupport.requireDifferent(origemId, destinoId, "origemId e destinoId devem ser diferentes.");
        BigDecimal valorValidado = ValidationSupport.validarValorMonetario(valor, "valor");

        // Lock both users in stable order to reduce deadlock risk.
        UUID primeiroId = origemId.compareTo(destinoId) < 0 ? origemId : destinoId;
        UUID segundoId = primeiroId.equals(origemId) ? destinoId : origemId;

        Usuario primeiro = usuarioRepository.findByIdForUpdate(primeiroId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
        Usuario segundo = usuarioRepository.findByIdForUpdate(segundoId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));

        Usuario origem = primeiro.getId().equals(origemId) ? primeiro : segundo;
        Usuario destino = origem == primeiro ? segundo : primeiro;

        origem.debitar(valorValidado);
        destino.creditar(valorValidado);

        usuarioRepository.save(origem);
        usuarioRepository.save(destino);

        OffsetDateTime agora = OffsetDateTime.now(clock);
        UUID transferenciaId = UUID.randomUUID();
        transferenciaRepository.save(new Transferencia(transferenciaId, origemId, destinoId, valorValidado, agora));

        transacaoRepository.save(new Transacao(UUID.randomUUID(), TransacaoTipo.TRANSFERENCIA_SAIDA, origemId, transferenciaId, valorValidado, agora));
        transacaoRepository.save(new Transacao(UUID.randomUUID(), TransacaoTipo.TRANSFERENCIA_ENTRADA, destinoId, transferenciaId, valorValidado, agora));

        return new TransferResult(origem.getSaldo(), destino.getSaldo());
    }

    public record TransferResult(BigDecimal saldoOrigem, BigDecimal saldoDestino) {
    }
}

