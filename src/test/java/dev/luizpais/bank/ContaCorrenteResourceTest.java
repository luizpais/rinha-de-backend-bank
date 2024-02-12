package dev.luizpais.bank;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class ContaCorrenteResourceTest {

    @Test
    void extrato() {
    }

    @Test
    void deveRetornar422ParaValorInvalido() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(0, "teste", "d"))
        .when().post("/clientes/1/transacoes")
        .then().statusCode(422);
    }

    @Test
    void deveRetornar422ParaTransacaoInvalida() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(1, "teste", "x"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar422ParaTransacaoNula() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(1, "teste", null))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar422ParaDescricaoVazia() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(1, "", "d"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar422ParaDescricaoComMaisDe10Caracteres() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(1, "textocom11c", "d"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar422ParaDescricaoNula() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(1, null, "d"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar404ParaClienteInexistenteNaTransacao() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(100, "teste", "d"))
        .when().post("/clientes/6/transacoes")
        .then().statusCode(404);
    }

    @Test
    void deveRetornar422ParaSaldoInvalido() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(9999999999L, "teste", "d"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar422ParaValorNaoInteiro() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"valor\": 1.2, \"tipo\": \"c\", \"descricao\": \"ok\"}")
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar422ParaValorTexto() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"valor\": \"ok\", \"tipo\": \"c\", \"descricao\": \"ok\"}")
                .when().post("/clientes/1/transacoes")
                .then().statusCode(422);
    }

    @Test
    void deveRetornar404ParaClienteInexistenteNoExtrato() {
        given()
                .contentType(ContentType.JSON)
                .when().get("/clientes/6/extrato")
                .then().statusCode(404);
    }

    @Test
    void deveRetornar200ParaTransacaoDeCreditoComSucesso() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(6666, "teste", "c"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(200);
    }

    @Test
    void deveRetornar200ParaTransacaoDeDebitoComSucesso1() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(6666, "teste", "d"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(200);
    }

    @Test
    void deveRetornar200ParaTransacaoDeDebitoComSucesso2() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(100, "teste", "d"))
                .when().post("/clientes/1/transacoes")
                .then().statusCode(200);
    }
}