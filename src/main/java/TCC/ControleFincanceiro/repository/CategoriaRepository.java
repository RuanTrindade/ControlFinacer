package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.enumerated.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioId(Long usuarioId);
    
    List<Categoria> findByUsuarioIdAndTipo(Long usuarioId, TipoTransacao tipo);

    boolean existsByNomeAndUsuarioId(String nome, Long usuarioId);

    Optional<Categoria> findByNomeAndPadraoSistemaTrue(String nome);
}
