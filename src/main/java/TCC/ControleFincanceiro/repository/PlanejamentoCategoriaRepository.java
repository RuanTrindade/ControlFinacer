package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.PlanejamentoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanejamentoCategoriaRepository extends JpaRepository<PlanejamentoCategoria, Long> {

    List<PlanejamentoCategoria> findByPlanejamentoMensalId(Long planejamentoMensalId);

    Optional<PlanejamentoCategoria> findByPlanejamentoMensalIdAndCategoriaId(Long planejamentoMensalId, Long categoriaId);

}
