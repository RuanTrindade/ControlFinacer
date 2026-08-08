package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Objetivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ObjetivoRepository extends JpaRepository<Objetivo, Long> {


    /*
     * Usado internamente por services que precisam
     * de todos os objetivos do usuário.
     */
    List<Objetivo> findByUsuarioId(
            Long usuarioId
    );


    /*
     * Usado pela tela de objetivos,
     * onde teremos paginação.
     */
    Page<Objetivo> findByUsuarioId(
            Long usuarioId,
            Pageable pageable
    );


    Page<Objetivo> findByUsuarioIdAndFinalizado(
            Long usuarioId,
            Boolean finalizado,
            Pageable pageable
    );

}
