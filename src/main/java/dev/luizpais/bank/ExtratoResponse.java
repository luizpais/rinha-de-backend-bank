package dev.luizpais.bank;

import java.time.LocalDateTime;
import java.util.List;

public record ExtratoResponse(
        SaldoAtual saldo,
        List<MovimentoDto> ultimas_transacoes) {

    record SaldoAtual(long total, long limite, LocalDateTime data_extrato) {
    }


}
