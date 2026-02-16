package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.PlanejamentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanejamentoMensalRepository extends JpaRepository<PlanejamentoMensal, Long> {

    Optional<PlanejamentoMensal> findByUsuarioIdAndReferencia(Long usuarioId, LocalDate referencia);

    List<PlanejamentoMensal> findByUsuarioIdOrderByReferenciaDesc(Long usuarioId);

}
