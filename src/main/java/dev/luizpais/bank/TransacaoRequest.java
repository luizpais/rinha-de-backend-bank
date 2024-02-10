package dev.luizpais.bank;

public record TransacaoRequest(
        long valor,
        String descricao,
        String tipo) {
}
