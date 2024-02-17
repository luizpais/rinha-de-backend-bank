package dev.luizpais.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.HashMap;

@Path("/")
public class ContaCorrenteResource {
    ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    ContaCorrenteService contaCorrenteService;

    @Path("clientes/{id}/extrato")
    @GET
    public Uni<ExtratoResponse> extrato(long id) {
        return contaCorrenteService.extrato(id).log("extrato");
    }

    @Path("clientes/{id}/transacoes")
    @POST
    public Uni<RestResponse<TransacaoResponse>> transacao(long id, String requestString) {
        TransacaoRequest request;
        Log.info("Recebendo transacao: " + requestString);
        try {
            JsonNode jsonNode = objectMapper.readTree(requestString);
            double valor = jsonNode.get("valor").asDouble();
            if (valor % 1 != 0) {
                Log.warn("Valor com centavos");
                return Uni.createFrom().item(RestResponse.status(422));
            }
            request = objectMapper.readValue(requestString, TransacaoRequest.class);
        } catch (JsonProcessingException e) {
            Log.error("Erro no parse do json");
            return Uni.createFrom().item(RestResponse.status(422));
        }

        if (request.descricao() == null || request.descricao().length() > 10 || request.descricao().isEmpty()
                || request.tipo() == null || (!request.tipo().equals("d") && !request.tipo().equals("c"))
                || request.valor() <= 0) {
            Log.warn("Erro no request");
            return Uni.createFrom().item(RestResponse.status(422));
        }
        Log.info("Transacao validada");
        return contaCorrenteService.transacao(id, request).log("transacao");
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(SaldoInsuficienteException e) {
        return RestResponse.status(422, "Saldo insuficiente");
    }
}
