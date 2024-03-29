package dev.quarkusclub.bank;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Movimento extends PanacheEntity {
    public long idCliente;
    public long valor;
    public String tipo;
    public String descricao;
    public LocalDateTime dataMovimento;

    public static Uni<List<Movimento>> findAteDezMovimentosByIdCliente(long idCliente) {
        return Movimento.find("idCliente", Sort.by("dataMovimento").descending(), idCliente)
                .page(Page.ofSize(10))
                .list();
    }
}