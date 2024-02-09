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
@NamedQueries({
        @NamedQuery(name = "Movimento.findByIdCliente",
                query = "SELECT m FROM Movimento m WHERE m.idCliente = ?1 " +
                        "order by m.dataMovimento desc " +
                        "LIMIT 10", lockMode = LockModeType.PESSIMISTIC_READ)
})
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