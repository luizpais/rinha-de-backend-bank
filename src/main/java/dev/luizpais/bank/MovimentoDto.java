package dev.luizpais.bank;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Movimento}
 */
public record MovimentoDto(String descricao, String tipo, long valor, LocalDateTime dataMovimento,
                           long saldo) implements Serializable {
}