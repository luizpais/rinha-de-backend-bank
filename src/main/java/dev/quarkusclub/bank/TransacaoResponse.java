package dev.quarkusclub.bank;

public record TransacaoResponse(
        long limite,
        long saldo) {
}
