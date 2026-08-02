package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    boolean existsByCategoriaId(Long categoriaId);

    Page<Transacao> findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(
            Long usuarioId,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    );

    @Query("""
       SELECT t FROM Transacao t
       WHERE t.usuario.id = :usuarioId
       AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)
       AND (:status IS NULL OR t.status = :status)
       AND (:metodoPagamento IS NULL OR t.metodoPagamento = :metodoPagamento)
       AND (:dataInicio IS NULL OR t.data >= :dataInicio)
       AND (:dataFim IS NULL OR t.data <= :dataFim)
       AND (:valorMin IS NULL OR t.valor >= :valorMin)
       AND (:valorMax IS NULL OR t.valor <= :valorMax)
       AND (
            :descricao IS NULL
            OR LOWER(t.descricao)
            LIKE LOWER(CONCAT('%', :descricao, '%'))
       )
       ORDER BY t.data DESC, t.id DESC
       """)
    Page<Transacao> filtrar(
            @Param("usuarioId")
            Long usuarioId,

            @Param("categoriaId")
            Long categoriaId,

            @Param("status")
            StatusPagamento status,

            @Param("metodoPagamento")
            MetodoPagamento metodoPagamento,

            @Param("dataInicio")
            LocalDate dataInicio,

            @Param("dataFim")
            LocalDate dataFim,

            @Param("valorMin")
            BigDecimal valorMin,

            @Param("valorMax")
            BigDecimal valorMax,

            @Param("descricao")
            String descricao,

            Pageable pageable
    );

    @Query("""
    SELECT COALESCE(SUM(
        CASE 
            WHEN t.categoria.tipo = 'RECEITA' THEN t.valor
            WHEN t.categoria.tipo = 'DESPESA' THEN -t.valor
        END
    ), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
""")
    BigDecimal calcularSaldoUsuario(Long usuarioId);


    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.categoria.id = :categoriaId
    AND t.categoria.tipo = 'DESPESA'
    AND t.status = :status
    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalPorCategoriaNoMesEStatus(
            @Param("usuarioId") Long usuarioId,
            @Param("categoriaId") Long categoriaId,
            @Param("status") StatusPagamento status,
            @Param("mes") int mes,
            @Param("ano") int ano
    );


    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.categoria.tipo = 'DESPESA'
    AND t.status = :status
    AND t.categoria.id IN (
        SELECT pc.categoria.id
        FROM PlanejamentoCategoria pc
        WHERE pc.planejamentoMensal.id = :planejamentoId
    )
    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalDespesasPlanejadasPorStatus(
            @Param("usuarioId") Long usuarioId,
            @Param("planejamentoId") Long planejamentoId,
            @Param("status") StatusPagamento status,
            @Param("mes") int mes,
            @Param("ano") int ano
    );

    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.categoria.tipo = 'DESPESA'
    AND t.status = :status
    AND t.categoria.id NOT IN (
        SELECT pc.categoria.id
        FROM PlanejamentoCategoria pc
        WHERE pc.planejamentoMensal.id = :planejamentoId
    )
    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalDespesasNaoPlanejadasPorStatus(
            @Param("usuarioId") Long usuarioId,
            @Param("planejamentoId") Long planejamentoId,
            @Param("status") StatusPagamento status,
            @Param("mes") int mes,
            @Param("ano") int ano
    );


    @Query("""
    SELECT 
        MONTH(t.data),
        YEAR(t.data),
        t.categoria.tipo,
        SUM(t.valor)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    GROUP BY YEAR(t.data), MONTH(t.data), t.categoria.tipo
    ORDER BY YEAR(t.data), MONTH(t.data)
""")
    List<Object[]> relatorioMensal(Long usuarioId);




    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.categoria.tipo = 'RECEITA'
    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalReceitasNoMes(
            @Param("usuarioId") Long usuarioId,
            @Param("mes") int mes,
            @Param("ano") int ano
    );

    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.categoria.tipo = 'RECEITA'
""")
    BigDecimal totalReceitas(Long usuarioId);



    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.categoria.tipo = 'DESPESA'
""")
    BigDecimal totalDespesas(Long usuarioId);
}
