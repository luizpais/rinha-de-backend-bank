package dev.luizpais.bank;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@Table(name = "conta_corrente")
@AllArgsConstructor
public class ContaCorrente extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "saldo")
    private long saldo;

    @Column(name = "limite")
    private long limite;

}