package dev.luizpais.bank;

import java.time.LocalDateTime;
import java.util.List;

;

record ExtratoResponse(SaldoAtual saldo, List<Transacao> ultimas_transacoes) {


    record SaldoAtual(long total, long limite, LocalDateTime data_extrato) {

    }

    record Transacao(String descricao, String tipo, long valor, LocalDateTime realizada_em) {

    }
}