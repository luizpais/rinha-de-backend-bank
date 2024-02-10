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
    void deveRetornar404ParaClienteInexistente() {
        given()
                .contentType(ContentType.JSON)
                .body(new TransacaoRequest(100, "teste", "d"))
        .when().post("/clientes/6/transacoes")
        .then().statusCode(404);
    }
}