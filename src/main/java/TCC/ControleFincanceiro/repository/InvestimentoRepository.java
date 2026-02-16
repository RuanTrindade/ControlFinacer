package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {

    List<Investimento> findByUsuarioId(Long usuarioId);
}
