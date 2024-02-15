package dev.luizpais.bank;

public record MovimentoDto(
        long valor,
        String tipo,
        String descricao,
        java.time.LocalDateTime data_movimento) {
}
