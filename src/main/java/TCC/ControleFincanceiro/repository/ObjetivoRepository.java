package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Objetivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjetivoRepository extends JpaRepository<Objetivo, Long> {

    List<Objetivo> findByUsuarioId(Long usuarioId);

    List<Objetivo> findByUsuarioIdOrderByDataFinalAsc(Long usuarioId);

}
