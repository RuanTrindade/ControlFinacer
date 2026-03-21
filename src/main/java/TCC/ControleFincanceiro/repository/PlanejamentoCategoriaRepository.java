package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.PlanejamentoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PlanejamentoCategoriaRepository extends JpaRepository<PlanejamentoCategoria, Long> {

    List<PlanejamentoCategoria> findByPlanejamentoMensalId(Long planejamentoId);

    @Query("""
        SELECT COALESCE(SUM(p.limite), 0)
        FROM PlanejamentoCategoria p
        WHERE p.planejamentoMensal.id = :planejamentoId
    """)
    BigDecimal somaLimites(Long planejamentoId);
}
