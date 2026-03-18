package com.kaique.transacao_api.usecase.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.kaique.transacao_api.domain.exceptions.BusinessRuleViolationException;

public final class ValidationSupport {

    private ValidationSupport() {
    }

    public static UUID requireId(UUID id, String campo) {
        if (id == null) {
            throw new BusinessRuleViolationException("O campo " + campo + " e obrigatorio.");
        }
        return id;
    }

    public static String validarNome(String nome) {
        String nomeTrim = Optional.ofNullable(nome).map(String::trim).orElse("");
        if (nomeTrim.isBlank()) {
            throw new BusinessRuleViolationException("O campo nome e obrigatorio.");
        }
        if (nomeTrim.length() > 120) {
            throw new BusinessRuleViolationException("O campo nome deve ter no maximo 120 caracteres.");
        }
        return nomeTrim;
    }

    public static String normalizarEmail(String email) {
        String emailTrim = Optional.ofNullable(email).map(String::trim).orElse("");
        if (emailTrim.isBlank()) {
            throw new BusinessRuleViolationException("O campo email e obrigatorio.");
        }
        if (emailTrim.length() > 254) {
            throw new BusinessRuleViolationException("O campo email deve ter no maximo 254 caracteres.");
        }
        String normalizado = emailTrim.toLowerCase(Locale.ROOT);
        if (!normalizado.contains("@") || normalizado.startsWith("@") || normalizado.endsWith("@")) {
            throw new BusinessRuleViolationException("Email invalido.");
        }
        return normalizado;
    }

    public static BigDecimal validarValorMonetario(BigDecimal valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleViolationException("O campo " + campo + " e obrigatorio.");
        }
        if (valor.scale() > 2) {
            throw new BusinessRuleViolationException("O campo " + campo + " deve ter no maximo 2 casas decimais.");
        }
        BigDecimal valorNormalizado = valor.setScale(2, RoundingMode.UNNECESSARY);
        if (valorNormalizado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("O campo " + campo + " deve ser maior que zero.");
        }
        return valorNormalizado;
    }

    public static BigDecimal validarValorNaoNegativo(Double valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleViolationException("O campo " + campo + " e obrigatorio.");
        }
        if (valor < 0) {
            throw new BusinessRuleViolationException("Valor da transacao deve ser maior ou igual a zero.");
        }
        return BigDecimal.valueOf(valor);
    }

    public static String requireNonBlank(String raw, String campo) {
        String v = Optional.ofNullable(raw).map(String::trim).orElse("");
        if (v.isBlank()) {
            throw new BusinessRuleViolationException("O campo " + campo + " e obrigatorio.");
        }
        return v;
    }

    public static void requireDifferent(UUID a, UUID b, String message) {
        if (Objects.equals(a, b)) {
            throw new BusinessRuleViolationException(message);
        }
    }
}

