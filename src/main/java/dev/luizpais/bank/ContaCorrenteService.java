package dev.luizpais.bank;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.ws.rs.NotFoundException;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;

@ApplicationScoped
public class ContaCorrenteService {
    @WithSession
    public Uni<ExtratoResponse> extrato(Long id) {
        return ContaCorrente.<ContaCorrente>findById(id)
                .onItem().ifNull().failWith(new NotFoundException())
                .chain(conta -> {
                    var extrato = new ExtratoResponse();
                    extrato.saldo = new ExtratoResponse.SaldoAtual(conta.saldo, LocalDateTime.now(), conta.limite);
                    return Movimento.findAteDezMovimentosByIdCliente(id)
                            .onItem().ifNotNull().transform(movimentos -> {
                                movimentos.forEach(movimento ->
                                        extrato.ultimas_transacoes.add(new ExtratoResponse.Transacao(movimento.valor, movimento.tipo, movimento.descricao, movimento.dataMovimento)));
                                return extrato;
                            });
                });
    }

    @WithTransaction
    public Uni<RestResponse<TransacaoResponse>> transacao(long id, TransacaoRequest request) {
        return ContaCorrente.<ContaCorrente>findById(id, LockModeType.PESSIMISTIC_WRITE)
                .onItem().ifNull().failWith(new NotFoundException())
                .call(Unchecked.function(contaCorrente -> {
                    var movimento = new Movimento();
                    if (request.tipo().equals("d")) {
                        if (contaCorrente.saldo - request.valor() < -contaCorrente.limite) {
                            throw new SaldoInsuficienteException();
                        }
                        contaCorrente.saldo = contaCorrente.saldo - request.valor();
                    } else if (request.tipo().equals("c")) {
                        contaCorrente.saldo = contaCorrente.saldo + request.valor();
                    }

                    movimento.dataMovimento = LocalDateTime.now();
                    movimento.descricao = request.descricao();
                    movimento.idCliente = contaCorrente.id;
                    movimento.tipo = request.tipo();
                    movimento.valor = request.valor();
                    return movimento.persist();
                }))
                .map(contaCorrente -> {
                    var response = new TransacaoResponse(contaCorrente.limite, contaCorrente.saldo);
                    return RestResponse.ResponseBuilder.ok(response).build();
                });
    }
}
