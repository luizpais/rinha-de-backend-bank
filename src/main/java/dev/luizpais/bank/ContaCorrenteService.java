package dev.luizpais.bank;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.stream.Collectors;

@ApplicationScoped
public class ContaCorrenteService {

    @Transactional
    public RestResponse<ExtratoResponse> extrato(Long id) {
        if(id > 5)
            return RestResponse.notFound();
        var conta = (ContaCorrente) ContaCorrente.findById(id);
        if (conta == null) {
            return RestResponse.notFound();
        }
        var extrato = new ExtratoResponse();
        var saldo = new ExtratoResponse.SaldoAtual();
        saldo.total = conta.getSaldo();
        saldo.limite = conta.getLimite();
        saldo.data_extrato = LocalDateTime.now();
        extrato.saldo = saldo;
        var movimentos = Movimento.findMovimentoByIdCliente(id);
        movimentos.ifPresent((movs) -> extrato.ultimas_transacoes = movs.stream().map(movimento -> {
            var transacao = new ExtratoResponse.Transacao();
            transacao.descricao = movimento.getDescricao();
            transacao.tipo = movimento.getTipo();
            transacao.valor = movimento.getValor();
            transacao.realizada_em = movimento.getDataMovimento();
            return transacao;
        }).collect(Collectors.toList()));
        return RestResponse.ResponseBuilder.ok(extrato).build();
    }


    @Transactional
    public RestResponse<TransacaoResponse> transacao(long id, TransacaoRequest request) throws SaldoInsuficienteException {
        var conta = (ContaCorrente) ContaCorrente.findById(id);
        if (conta == null) {
            return RestResponse.notFound();
        }
        var movimento = new Movimento();
        if (request.tipo().equals("d")) {
            if (conta.getSaldo() - request.valor() < -conta.getLimite()) {
                throw new SaldoInsuficienteException("Saldo insuficiente");
            }
            conta.setSaldo(conta.getSaldo() - request.valor());
        } else if (request.tipo().equals("c")) {
            conta.setSaldo(conta.getSaldo() + request.valor());
        }
        Calendar calendar = Calendar.getInstance();

        movimento.setDataMovimento(LocalDateTime.now());
        movimento.setDescricao(request.descricao());
        movimento.setIdCliente(conta.getId());
        movimento.setTipo(request.tipo());
        movimento.setValor(request.valor());
        movimento.persist();

        conta.persist();
        var response = new TransacaoResponse(conta.getLimite(), conta.getSaldo());
        return RestResponse.ResponseBuilder.ok(response).build();

    }
}
