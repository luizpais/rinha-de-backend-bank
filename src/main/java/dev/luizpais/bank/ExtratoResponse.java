package dev.luizpais.bank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExtratoResponse {
    public SaldoAtual saldo;
    public List<Transacao> ultimas_transacoes = new ArrayList<>();

    record SaldoAtual (
        long total,
        LocalDateTime data_extrato,
        long limite){
    }

    record Transacao (
        long valor,
        String tipo,
        String descricao,
        LocalDateTime realizada_em){
    }
}
