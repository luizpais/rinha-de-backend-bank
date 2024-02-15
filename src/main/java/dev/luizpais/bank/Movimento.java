package dev.luizpais.bank;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.ws.rs.QueryParam;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@Entity
@Table(name = "movimentos")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Movimento.findByIdCliente",
                query = "SELECT m.descricao, m.tipo, m.valor, m.data_movimento, m.saldo " +
                        "FROM movimentos m WHERE m.id_cliente = ?1 " +
                        "order by m.data_movimento desc " +
                        "LIMIT 10",
                resultSetMapping = "MovimentoMapping")

})

@SqlResultSetMapping(
        name = "MovimentoMapping",
        classes = @ConstructorResult(
                targetClass = MovimentoDto.class,
                columns = {
                        @ColumnResult(name = "descricao", type = String.class),
                        @ColumnResult(name = "tipo", type = String.class),
                        @ColumnResult(name = "valor", type = Long.class),
                        @ColumnResult(name = "data_movimento", type = LocalDateTime.class),
                        @ColumnResult(name = "saldo", type = Long.class)
                })
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movimento extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_cliente")
    private long idCliente;

    @Column(name = "valor")
    private long valor;

    @Column(name = "tipo", length = 1)
    private String tipo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_movimento")
    private LocalDateTime dataMovimento;

    public static Optional<List<Movimento>> findMovimentoByIdCliente(long idCliente) {
        return Optional.ofNullable(list("#Movimento.findByIdCliente", idCliente));
    }


}