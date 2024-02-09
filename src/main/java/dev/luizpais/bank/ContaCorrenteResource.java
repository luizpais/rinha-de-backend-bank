package dev.luizpais.bank;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import javax.naming.ldap.ExtendedResponse;

@Path("/")
@Slf4j
public class ContaCorrenteResource {

    @Inject
    ContaCorrenteService contaCorrenteService;

    @Path("clientes/{id}/extrato")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public RestResponse<ExtratoResponse> extrato(Long id) {
        log.info("Extrato para o cliente: {}", id);
        return contaCorrenteService.extrato(id);
    }

    @Path("clientes/{id}/transacoes")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public RestResponse<TransacaoResponse> transacao(long id, TransacaoRequest request) {
        if((request.descricao != null && request.descricao.length() > 10)
        || (request.tipo != null && !request.tipo.equals("d") && !request.tipo.equals("c")
        || (request.valor <= 0))) {
            return RestResponse.status(422);
        }
        try {
            return contaCorrenteService.transacao(id, request);
        } catch (SaldoInsuficienteException e) {
            return RestResponse.status(402);
        }

    }
}
