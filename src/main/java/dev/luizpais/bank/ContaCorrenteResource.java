package dev.luizpais.bank;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.HashMap;

@Path("/")
@Slf4j
public class ContaCorrenteResource {

    HashMap<Long, RestResponse<ExtratoResponse>> extratos = new HashMap<>();

    @Inject
    ContaCorrenteService contaCorrenteService;

    @Path("clientes/{id}/extrato")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public RestResponse<ExtratoResponse> extrato(Long id) {
        log.info("Extrato para o cliente: {}", id);
        var extrato = extratos.get(id);
        if (extrato == null) {
            extrato = contaCorrenteService.extrato(id);
            extratos.put(id, extrato);
        }
        return extrato;
    }

    @Path("clientes/{id}/transacoes")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public RestResponse<TransacaoResponse> transacao(long id, TransacaoRequest request) {
        if ((request.descricao() == null)
                || (request.descricao().length() > 10 || request.descricao().isEmpty())
                || (request.tipo() == null)
                || (!request.tipo().equals("d") && !request.tipo().equals("c"))
                || (request.valor() <= 0)
                || (request.valor() - (long) request.valor() > 0.0)) {
            return RestResponse.status(422);
        }
        try {
            var retorno = contaCorrenteService.transacao(id, request);
            extratos.remove(id);
            return retorno;
        } catch (SaldoInsuficienteException e) {
            return RestResponse.status(422);
        }

    }
}
