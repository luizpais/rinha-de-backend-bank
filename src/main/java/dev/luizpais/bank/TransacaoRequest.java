package dev.luizpais.bank;

public record TransacaoRequest (
        double valor,
        String descricao,
        String tipo){
}
