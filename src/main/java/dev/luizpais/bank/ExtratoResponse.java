package dev.luizpais.bank;
;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtratoResponse {

    public SaldoAtual saldo;
    public List<Transacao> ultimas_transacoes;
    static class SaldoAtual {
        public long total;
        public long limite;

        public LocalDateTime data_extrato;
    }

    static class Transacao {
        public String descricao;
        public String tipo;
        public long valor;
        public LocalDateTime realizada_em;
    }

}
