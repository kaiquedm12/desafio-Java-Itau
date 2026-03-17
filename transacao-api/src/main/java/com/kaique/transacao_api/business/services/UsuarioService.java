package com.kaique.transacao_api.business.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.business.models.Usuario;
import com.kaique.transacao_api.infrastructure.exceptions.NotFoundException;
import com.kaique.transacao_api.infrastructure.exceptions.UnprocessableEntity;

@Service
public class UsuarioService {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private final Map<UUID, Usuario> usuariosPorId = new ConcurrentHashMap<>();
    private final Map<String, UUID> usuarioIdPorEmailNormalizado = new ConcurrentHashMap<>();

    public Usuario criarUsuario(String nome, String email) {
        String nomeValidado = validarNome(nome);
        String emailNormalizado = normalizarEmail(email);

        UUID novoId = UUID.randomUUID();
        UUID existente = usuarioIdPorEmailNormalizado.putIfAbsent(emailNormalizado, novoId);
        if (existente != null) {
            throw new UnprocessableEntity("Email ja cadastrado.");
        }

        Usuario usuario = new Usuario(novoId, nomeValidado, emailNormalizado, ZERO);
        usuariosPorId.put(novoId, usuario);
        return usuario;
    }

    public Usuario buscarUsuario(UUID id) {
        if (id == null) {
            throw new UnprocessableEntity("O campo id e obrigatorio.");
        }
        Usuario usuario = usuariosPorId.get(id);
        if (usuario == null) {
            throw new NotFoundException("Usuario nao encontrado.");
        }
        return usuario;
    }

    public BigDecimal consultarSaldo(UUID id) {
        return buscarUsuario(id).getSaldo();
    }

    public BigDecimal depositar(UUID id, BigDecimal valor) {
        BigDecimal valorValidado = validarValorMonetario(valor, "valor");
        Usuario usuario = buscarUsuario(id);

        usuario.lock().lock();
        try {
            usuario.setSaldo(usuario.getSaldo().add(valorValidado));
            return usuario.getSaldo();
        } finally {
            usuario.lock().unlock();
        }
    }

    public TransferenciaResult transferir(UUID origemId, UUID destinoId, BigDecimal valor) {
        if (origemId == null) {
            throw new UnprocessableEntity("O campo origemId e obrigatorio.");
        }
        if (destinoId == null) {
            throw new UnprocessableEntity("O campo destinoId e obrigatorio.");
        }
        if (Objects.equals(origemId, destinoId)) {
            throw new UnprocessableEntity("origemId e destinoId devem ser diferentes.");
        }

        BigDecimal valorValidado = validarValorMonetario(valor, "valor");

        Usuario origem = buscarUsuario(origemId);
        Usuario destino = buscarUsuario(destinoId);

        Usuario primeiro = origem.getId().compareTo(destino.getId()) < 0 ? origem : destino;
        Usuario segundo = primeiro == origem ? destino : origem;

        primeiro.lock().lock();
        segundo.lock().lock();
        try {
            if (origem.getSaldo().compareTo(valorValidado) < 0) {
                throw new UnprocessableEntity("Saldo insuficiente.");
            }

            origem.setSaldo(origem.getSaldo().subtract(valorValidado));
            destino.setSaldo(destino.getSaldo().add(valorValidado));
            return new TransferenciaResult(origem.getSaldo(), destino.getSaldo());
        } finally {
            segundo.lock().unlock();
            primeiro.lock().unlock();
        }
    }

    private String validarNome(String nome) {
        String nomeTrim = Optional.ofNullable(nome).map(String::trim).orElse("");
        if (nomeTrim.isBlank()) {
            throw new UnprocessableEntity("O campo nome e obrigatorio.");
        }
        if (nomeTrim.length() > 120) {
            throw new UnprocessableEntity("O campo nome deve ter no maximo 120 caracteres.");
        }
        return nomeTrim;
    }

    private String normalizarEmail(String email) {
        String emailTrim = Optional.ofNullable(email).map(String::trim).orElse("");
        if (emailTrim.isBlank()) {
            throw new UnprocessableEntity("O campo email e obrigatorio.");
        }
        if (emailTrim.length() > 254) {
            throw new UnprocessableEntity("O campo email deve ter no maximo 254 caracteres.");
        }
        // Normalizacao basica para unicidade (nao e validador RFC).
        String normalizado = emailTrim.toLowerCase(Locale.ROOT);
        if (!normalizado.contains("@") || normalizado.startsWith("@") || normalizado.endsWith("@")) {
            throw new UnprocessableEntity("Email invalido.");
        }
        return normalizado;
    }

    private BigDecimal validarValorMonetario(BigDecimal valor, String campo) {
        if (valor == null) {
            throw new UnprocessableEntity("O campo " + campo + " e obrigatorio.");
        }
        if (valor.scale() > 2) {
            throw new UnprocessableEntity("O campo " + campo + " deve ter no maximo 2 casas decimais.");
        }
        BigDecimal valorNormalizado = valor.setScale(2, RoundingMode.UNNECESSARY);
        if (valorNormalizado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnprocessableEntity("O campo " + campo + " deve ser maior que zero.");
        }
        return valorNormalizado;
    }

    public record TransferenciaResult(BigDecimal saldoOrigem, BigDecimal saldoDestino) {
    }
}

