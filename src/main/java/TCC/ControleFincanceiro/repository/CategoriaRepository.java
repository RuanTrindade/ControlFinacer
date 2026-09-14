package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.enumerated.TipoTransacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {


    List<Categoria> findByUsuarioId(
            Long usuarioId
    );


    List<Categoria> findByUsuarioIdOrPadraoSistemaTrue(
            Long usuarioId
    );


    List<Categoria> findByUsuarioIdAndTipo(
            Long usuarioId,
            TipoTransacao tipo
    );


    boolean existsByNomeAndUsuarioId(
            String nome,
            Long usuarioId
    );


    Optional<Categoria> findByNomeAndPadraoSistemaTrue(
            String nome
    );


    /* ===================================================
       LISTAGEM PAGINADA POR TIPO
       Usada na tela de Categorias
    =================================================== */

    @Query("""
        SELECT c
        FROM Categoria c
        WHERE
            (
                c.usuario.id = :usuarioId
                OR c.padraoSistema = true
            )
            AND (
                c.internaSistema = false
                OR c.internaSistema IS NULL
            )
            AND c.tipo = :tipo
        ORDER BY c.nome
    """)
    Page<Categoria> listarCategoriasDisponiveisPaginado(

            @Param("usuarioId")
            Long usuarioId,

            @Param("tipo")
            TipoTransacao tipo,

            Pageable pageable

    );


    /* ===================================================
       LISTAGEM COMPLETA
       Usada em selects, formulário de transação,
       planejamento etc.
    =================================================== */

    @Query("""
        SELECT c
        FROM Categoria c
        WHERE
            (
                c.usuario.id = :usuarioId
                OR c.padraoSistema = true
            )
            AND (
                c.internaSistema = false
                OR c.internaSistema IS NULL
            )
        ORDER BY c.nome
    """)
    List<Categoria> listarCategoriasDisponiveis(

            @Param("usuarioId")
            Long usuarioId

    );

}