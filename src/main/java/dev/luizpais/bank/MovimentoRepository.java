package dev.luizpais.bank;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class MovimentoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<MovimentoDto> loadYourData(long id_cliente) {
        String sql = "SELECT new dev.luizpais.bank.MovimentoDto(m.valor, m.tipo, m.descricao, m.dataMovimento) " +
                "FROM Movimento m " +
                "WHERE m.idCliente = :id_cliente " +
                "ORDER BY m.dataMovimento desc " +
                "limit 10";
        TypedQuery<MovimentoDto> query = entityManager.createQuery(sql, MovimentoDto.class);
        query.setParameter("id_cliente", id_cliente);
        return query.getResultList();
    }
}
