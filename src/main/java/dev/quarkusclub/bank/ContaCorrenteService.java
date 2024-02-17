package dev.quarkusclub.bank;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ContaCorrenteService {

    @Inject
    EntityManager entityManager;

    @WithTransaction
    public Uni<ExtratoResponse> extrato(Long id) {
        if (id > 5)
            throw new NotFoundException();


        return ContaCorrente.findById(id).onItem().ifNotNull().transformToUni(conta -> {
            var saldo = new ExtratoResponse.SaldoAtual(   ((ContaCorrente) conta).getSaldo(), ((ContaCorrente) conta).getLimite(), LocalDateTime.now());

            Uni<List> uni = Uni.createFrom().item(() -> {
                // Make a remote synchronous call
                return entityManager.createNativeQuery("Movimento.findByIdCliente", MovimentoDto.class).getResultList();
            }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());

            var movimentos = uni.onItem().transformToUni(movimento -> {
                return Uni.createFrom().item(movimento);
            });
            return  ExtratoResponse(saldo, movimentos);

//            if (movimentos.isPresent()) {
//                return movimentos.get().onItem().transformToUni(movimento -> {
//                    ExtratoResponse extrato = new ExtratoResponse( saldo, movimento.stream().map(mov -> new ExtratoResponse.Transacao(
//                     mov.getDescricao(), mov.getTipo(), mov.getValor(), mov.getDataMovimento())).collect(Collectors.toList()));
//                    return Uni.createFrom().item(extrato);
//                });
//            } else {
//                return Uni.createFrom().item(new ExtratoResponse(saldo, null));
//            }

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
            if (request.tipo().equals("d")) {
                if (conta.getSaldo() - request.valor() < -conta.getLimite()) {
                    throw new SaldoInsuficienteException("Saldo insuficiente");
                }
                conta.setSaldo(conta.getSaldo() - (long) request.valor());
            } else if (request.tipo().equals("c")) {
                conta.setSaldo(conta.getSaldo() + (long) request.valor());
            }
            movimento.setDataMovimento(LocalDateTime.now());
            movimento.setDescricao(request.descricao());
            movimento.setIdCliente(conta.getId());
            movimento.setTipo(request.tipo());
            movimento.setValor((long) request.valor());
            movimento.persist();

            conta.persist();
            var response = new TransacaoResponse(conta.getLimite(), conta.getSaldo());

            return Uni.createFrom().item(RestResponse.ok(response));
        }).onItem().ifNull().continueWith(() -> {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        });

    }
}
