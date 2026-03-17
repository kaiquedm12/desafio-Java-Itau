package com.kaique.transacao_api.business.models;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class Usuario {

    private final UUID id;
    private final String nome;
    private final String email;
    private BigDecimal saldo;
    private final ReentrantLock lock = new ReentrantLock();

    public Usuario(UUID id, String nome, String email, BigDecimal saldoInicial) {
        this.id = Objects.requireNonNull(id, "id");
        this.nome = Objects.requireNonNull(nome, "nome");
        this.email = Objects.requireNonNull(email, "email");
        this.saldo = Objects.requireNonNull(saldoInicial, "saldoInicial");
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = Objects.requireNonNull(saldo, "saldo");
    }

    public ReentrantLock lock() {
        return lock;
    }
}

