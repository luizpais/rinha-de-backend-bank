package dev.luizpais.bank;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class TransacaoRequest {

    public double valor;
    public String descricao;
    public String tipo;
}
