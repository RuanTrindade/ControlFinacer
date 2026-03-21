package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    @Query("""
           SELECT t FROM Transacao t
           WHERE (:usuarioId IS NULL OR t.usuario.id = :usuarioId)
           AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)
           AND (:status IS NULL OR t.status = :status)
           AND (:metodoPagamento IS NULL OR t.metodoPagamento = :metodoPagamento)
           AND (:dataInicio IS NULL OR t.data >= :dataInicio)
           AND (:dataFim IS NULL OR t.data <= :dataFim)
           AND (:valorMin IS NULL OR t.valor >= :valorMin)
           AND (:valorMax IS NULL OR t.valor <= :valorMax)
           AND (:descricao IS NULL OR LOWER(t.descricao) 
                LIKE LOWER(CONCAT('%', :descricao, '%')))
           """)
    List<Transacao> filtrar(
            Long usuarioId,
            Long categoriaId,
            StatusPagamento status,
            MetodoPagamento metodoPagamento,
            LocalDate dataInicio,
            LocalDate dataFim,
            BigDecimal valorMin,
            BigDecimal valorMax,
            String descricao
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
    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalPorCategoriaNoMes(Long usuarioId, Long categoriaId, int mes, int ano);

    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.categoria.id IN (
        SELECT pc.categoria.id
        FROM PlanejamentoCategoria pc
        WHERE pc.planejamentoMensal.id = :planejamentoId
    )
    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalDespesasPlanejadas(
            Long usuarioId,
            Long planejamentoId,
            int mes,
            int ano
    );
}
