package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.entity.enumerated.TipoTransacao;
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
   AND (:tipo IS NULL OR t.categoria.tipo = :tipo)
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

            @Param("tipo")
            TipoTransacao tipo,

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
    AND t.status = :status
""")
    BigDecimal calcularSaldoUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("status") StatusPagamento status
    );

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

AND (
    t.categoria.internaSistema = false
    OR t.categoria.internaSistema IS NULL
)

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

    /* ===================================================
   DASHBOARD - SALDO ACUMULADO ATÉ A DATA
=================================================== */

    @Query("""
    SELECT COALESCE(
        SUM(
            CASE
                WHEN t.categoria.tipo = 'RECEITA'
                    THEN t.valor

                WHEN t.categoria.tipo = 'DESPESA'
                    THEN -t.valor

                ELSE 0
            END
        ),
        0
    )
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
    AND t.status = :status
    AND t.data <= :dataFim
""")
    BigDecimal calcularSaldoUsuarioAteData(

            @Param("usuarioId")
            Long usuarioId,

            @Param("status")
            StatusPagamento status,

            @Param("dataFim")
            LocalDate dataFim

    );


/* ===================================================
   DASHBOARD - RECEITAS DO MÊS
=================================================== */

    /*
     * Consideramos somente receitas PAGAS.
     *
     * Resgates de investimento e objetivo
     * entram no saldo da conta, porém não são
     * considerados uma nova receita/renda
     * no card Receita do Dashboard.
     */
    @Query("""
    SELECT COALESCE(
        SUM(t.valor),
        0
    )
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId

    AND t.categoria.tipo = 'RECEITA'

    AND t.status = 'PAGO'

    AND t.categoria.nome NOT IN (
        'Resgate de Investimento',
        'Resgate de Objetivo'
    )

    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalReceitasDashboardNoMes(

            @Param("usuarioId")
            Long usuarioId,

            @Param("mes")
            int mes,

            @Param("ano")
            int ano

    );


/* ===================================================
   DASHBOARD - DESPESAS DO MÊS
=================================================== */

    /*
     * Consideramos somente despesas PAGAS.
     *
     * Investimentos possuem card próprio.
     *
     * Objetivos representam dinheiro guardado
     * pelo usuário e não consumo normal,
     * portanto também não entram no card
     * Despesas.
     */
    @Query("""
    SELECT COALESCE(
        SUM(t.valor),
        0
    )
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId

    AND t.categoria.tipo = 'DESPESA'

    AND t.status = 'PAGO'

    AND t.categoria.nome NOT IN (
        'Investimentos',
        'Objetivos'
    )

    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalDespesasDashboardNoMes(

            @Param("usuarioId")
            Long usuarioId,

            @Param("mes")
            int mes,

            @Param("ano")
            int ano

    );


/* ===================================================
   DASHBOARD - TOTAL DE CATEGORIA DO SISTEMA NO MÊS
=================================================== */

    /*
     * Usado inicialmente para pegar:
     *
     * "Investimentos"
     *
     * Como os aportes criam uma transação
     * nessa categoria padrão, conseguimos
     * descobrir quanto foi investido
     * naquele mês sem recalcular movimentações.
     */
    @Query("""
    SELECT COALESCE(
        SUM(t.valor),
        0
    )
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId

    AND t.categoria.nome = :nomeCategoria

    AND t.categoria.padraoSistema = true

    AND t.status = :status

    AND MONTH(t.data) = :mes
    AND YEAR(t.data) = :ano
""")
    BigDecimal totalCategoriaSistemaNoMes(

            @Param("usuarioId")
            Long usuarioId,

            @Param("nomeCategoria")
            String nomeCategoria,

            @Param("status")
            StatusPagamento status,

            @Param("mes")
            int mes,

            @Param("ano")
            int ano

    );
}
