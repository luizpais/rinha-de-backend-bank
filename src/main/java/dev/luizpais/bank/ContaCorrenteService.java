package dev.luizpais.bank;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ApplicationScoped
public class ContaCorrenteService {

    @WithTransaction
    public Uni<ExtratoResponse> extrato(Long id) {
        if (id > 5)
            throw new NotFoundException();

        var extrato = new ExtratoResponse();

        return ContaCorrente.findById(id).onItem().ifNotNull().transformToUni(conta -> {
            var saldo = new ExtratoResponse.SaldoAtual();
            saldo.total = ((ContaCorrente) conta).getSaldo();
            saldo.limite = ((ContaCorrente) conta).getLimite();
            saldo.data_extrato = LocalDateTime.now();
            extrato.saldo = saldo;

            var movimentos = Movimento.findMovimentoByIdCliente(id);
            if (movimentos.isPresent()) {
                return movimentos.get().onItem().transformToUni(movimento -> {
                    extrato.ultimas_transacoes = movimento.stream().map(mov -> {
                        var transacao = new ExtratoResponse.Transacao();
                        transacao.realizada_em = mov.getDataMovimento();
                        transacao.descricao = mov.getDescricao();
                        transacao.tipo = mov.getTipo();
                        transacao.valor = mov.getValor();
                        return transacao;
                    }).collect(Collectors.toList());
                    return Uni.createFrom().item(extrato);
                });
            } else {
                return Uni.createFrom().item(extrato);
            }

        }).onItem().ifNull().failWith(new NotFoundException());
    }


    @WithTransaction
    public Uni<RestResponse<TransacaoResponse>> transacao(long id, TransacaoRequest request) throws SaldoInsuficienteException {
        if (id > 5)
            return Uni.createFrom().item(RestResponse.notFound());
        var contaUni = ContaCorrente.findById(id);
        return contaUni.onItem().transformToUni(cta -> {

            var conta = (ContaCorrente) cta;
            var movimento = new Movimento();
            if (request.tipo.equals("d")) {
                if (conta.getSaldo() - request.valor < -conta.getLimite()) {
                    throw new SaldoInsuficienteException("Saldo insuficiente");
                }
                conta.setSaldo(conta.getSaldo() - (long) request.valor);
            } else if (request.tipo.equals("c")) {
                conta.setSaldo(conta.getSaldo() + (long) request.valor);
            }
            movimento.setDataMovimento(LocalDateTime.now());
            movimento.setDescricao(request.descricao);
            movimento.setIdCliente(conta.getId());
            movimento.setTipo(request.tipo);
            movimento.setValor((long) request.valor);
            movimento.persist();

            conta.persist();
            var response = new TransacaoResponse();
            response.limite = conta.getLimite();
            response.saldo = conta.getSaldo();
            return Uni.createFrom().item(RestResponse.ok(response));
        }).onItem().ifNull().continueWith(() -> {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        });

    }
}
