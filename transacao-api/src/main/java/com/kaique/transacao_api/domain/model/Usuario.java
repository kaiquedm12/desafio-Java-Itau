package com.kaique.transacao_api.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import com.kaique.transacao_api.domain.exceptions.BusinessRuleViolationException;

public class Usuario {

    private final UUID id;
    private final String nome;
    private final String email;
    private final String passwordHash;
    private BigDecimal saldo;

    public Usuario(UUID id, String nome, String email, String passwordHash, BigDecimal saldo) {
        this.id = Objects.requireNonNull(id, "id");
        this.nome = Objects.requireNonNull(nome, "nome");
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.saldo = Objects.requireNonNull(saldo, "saldo");
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void creditar(BigDecimal valor) {
        this.saldo = Objects.requireNonNull(valor, "valor").add(this.saldo);
    }

    public void debitar(BigDecimal valor) {
        Objects.requireNonNull(valor, "valor");
        if (saldo.compareTo(valor) < 0) {
            throw new BusinessRuleViolationException("Saldo insuficiente.");
        }
        this.saldo = this.saldo.subtract(valor);
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = Objects.requireNonNull(saldo, "saldo");
    }
}

