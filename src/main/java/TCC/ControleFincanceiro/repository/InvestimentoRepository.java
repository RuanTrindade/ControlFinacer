package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Investimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {

    List<Investimento> findByUsuarioId(Long usuarioId);

    long countByUsuarioIdAndEncerradoFalse(
            Long usuarioId
    );

    Page<Investimento> findByUsuarioIdAndEncerrado(
            Long usuarioId,
            Boolean encerrado,
            Pageable pageable
    );
}
