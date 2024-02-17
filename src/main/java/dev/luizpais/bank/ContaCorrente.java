package dev.luizpais.bank;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class ContaCorrente extends PanacheEntity {
    public String nome;
    public long saldo;
    public long limite;
}