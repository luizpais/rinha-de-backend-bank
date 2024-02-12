package dev.luizpais.bank;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.HashMap;

@Path("/")
@Slf4j
public class ContaCorrenteResource {

    HashMap<Long, Uni<RestResponse<ExtratoResponse>>> extratos = new HashMap<>();

    @Inject
    ContaCorrenteService contaCorrenteService;

    @Path("clientes/{id}/extrato")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Uni<ExtratoResponse> extrato(Long id) {
        log.info("Extrato para o cliente: {}", id);
        return contaCorrenteService.extrato(id);
    }

    @Path("clientes/{id}/transacoes")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Uni<RestResponse<TransacaoResponse>> transacao(long id, TransacaoRequest request) {
        if ((request.descricao == null)
            || (request.descricao.length() > 10 || request.descricao.isEmpty())
                || (request.tipo == null)
                || (!request.tipo.equals("d") && !request.tipo.equals("c"))
                || (request.valor <= 0)
                || (request.valor - (long) request.valor > 0.0)) {
            return Uni.createFrom().item(RestResponse.status(422));
        }
        var retorno = contaCorrenteService.transacao(id, request);
        return retorno.onFailure().recoverWithItem(ex -> {
            if (ex instanceof SaldoInsuficienteException) {
                return RestResponse.status(422);
            }
            return RestResponse.status(500);
        }).onItem().transformToUni(item -> Uni.createFrom().item(item));

    }
}
