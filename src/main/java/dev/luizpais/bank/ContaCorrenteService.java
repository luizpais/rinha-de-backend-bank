package dev.luizpais.bank;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.Calendar;

@ApplicationScoped
public class ContaCorrenteService {

    @Inject
    MovimentoRepository movimentoRepository;

    @Transactional
    public RestResponse<ExtratoResponse> extrato(Long id) {
        if(id > 5)
            return RestResponse.notFound();
        var conta = (ContaCorrente) ContaCorrente.findById(id);
        if (conta == null) {
            return RestResponse.notFound();
        }
        var saldo = new ExtratoResponse.SaldoAtual(
                conta.getSaldo(),
                conta.getLimite(),
                LocalDateTime.now());

        var movimentos = movimentoRepository.loadYourData(id);
        var extrato = new ExtratoResponse(saldo, movimentos);
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
            conta.setSaldo(conta.getSaldo() - (long)request.valor());
        } else if (request.tipo().equals("c")) {
            conta.setSaldo(conta.getSaldo() + (long)request.valor());
        }
        Calendar calendar = Calendar.getInstance();

        movimento.setDataMovimento(LocalDateTime.now());
        movimento.setDescricao(request.descricao());
        movimento.setIdCliente(conta.getId());
        movimento.setTipo(request.tipo());
        movimento.setValor((long)request.valor());
        movimento.persist();

        conta.persist();
        var response = new TransacaoResponse(conta.getLimite(),
            conta.getSaldo());
        return RestResponse.ResponseBuilder.ok(response).build();

    }
}
