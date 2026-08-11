package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.InvestimentoMovimentacao;
import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.Optional;


public interface InvestimentoMovimentacaoRepository
        extends JpaRepository<
        InvestimentoMovimentacao,
        Long
        > {


    /* ===================================================
       LISTAGEM
    =================================================== */

    Page<InvestimentoMovimentacao>
    findByInvestimentoIdOrderByDataDesc(

            Long investimentoId,

            Pageable pageable

    );


    @Query("""
    SELECT m
    FROM InvestimentoMovimentacao m

    WHERE m.investimento.id = :investimentoId

    AND m.data >= :inicio

    AND m.data < :fim

    AND (
        :tipo IS NULL
        OR m.tipo = :tipo
    )

    ORDER BY m.data DESC
""")
    Page<InvestimentoMovimentacao>
    listarMovimentacoesFiltradas(

            @Param("investimentoId")
            Long investimentoId,

            @Param("tipo")
            TipoInvestimento tipo,

            @Param("inicio")
            LocalDateTime inicio,

            @Param("fim")
            LocalDateTime fim,

            Pageable pageable

    );


    Optional<InvestimentoMovimentacao>
    findByTransacaoId(
            Long transacaoId
    );


    boolean existsByInvestimentoId(
            Long investimentoId
    );


    /* ===================================================
       TOTAL DE APORTES
    =================================================== */

    @Query("""
        SELECT COALESCE(
            SUM(m.valor),
            0
        )
        FROM InvestimentoMovimentacao m
        WHERE m.investimento.id = :investimentoId
        AND m.tipo =
            TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento.APORTE
    """)
    BigDecimal totalAportes(

            @Param("investimentoId")
            Long investimentoId

    );


    /* ===================================================
       TOTAL DE RESGATES
    =================================================== */

    @Query("""
        SELECT COALESCE(
            SUM(m.valor),
            0
        )
        FROM InvestimentoMovimentacao m
        WHERE m.investimento.id = :investimentoId
        AND m.tipo =
            TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento.RESGATE
    """)
    BigDecimal totalResgates(

            @Param("investimentoId")
            Long investimentoId

    );


    /* ===================================================
       TOTAL DE RENDIMENTOS
    =================================================== */

    @Query("""
        SELECT COALESCE(
            SUM(m.valor),
            0
        )
        FROM InvestimentoMovimentacao m
        WHERE m.investimento.id = :investimentoId
        AND m.tipo =
            TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento.RENDIMENTO
    """)
    BigDecimal totalRendimentos(

            @Param("investimentoId")
            Long investimentoId

    );


    /* ===================================================
       TOTAL DE UM TIPO SOMENTE NO MÊS
    =================================================== */

    @Query("""
        SELECT COALESCE(
            SUM(m.valor),
            0
        )
        FROM InvestimentoMovimentacao m
        WHERE m.investimento.id = :investimentoId
        AND m.tipo = :tipo
        AND m.data >= :inicio
        AND m.data < :fim
    """)
    BigDecimal totalPorTipoPeriodo(

            @Param("investimentoId")
            Long investimentoId,

            @Param("tipo")
            TipoInvestimento tipo,

            @Param("inicio")
            LocalDateTime inicio,

            @Param("fim")
            LocalDateTime fim

    );


    /* ===================================================
       TOTAL ACUMULADO ATÉ DETERMINADO MÊS
    =================================================== */

    @Query("""
        SELECT COALESCE(
            SUM(m.valor),
            0
        )
        FROM InvestimentoMovimentacao m
        WHERE m.investimento.id = :investimentoId
        AND m.tipo = :tipo
        AND m.data < :fim
    """)
    BigDecimal totalPorTipoAtePeriodo(

            @Param("investimentoId")
            Long investimentoId,

            @Param("tipo")
            TipoInvestimento tipo,

            @Param("fim")
            LocalDateTime fim

    );


    /* ===================================================
       VERIFICAR RENDIMENTO NO MÊS
    ===================================================

       Usaremos para permitir somente
       UM rendimento por investimento
       em cada mês.
    =================================================== */

    @Query("""
        SELECT CASE
            WHEN COUNT(m) > 0
                THEN true
            ELSE false
        END
        FROM InvestimentoMovimentacao m
        WHERE m.investimento.id = :investimentoId
        AND m.tipo =
            TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento.RENDIMENTO
        AND m.data >= :inicio
        AND m.data < :fim
    """)
    boolean existeRendimentoNoPeriodo(

            @Param("investimentoId")
            Long investimentoId,

            @Param("inicio")
            LocalDateTime inicio,

            @Param("fim")
            LocalDateTime fim

    );


    /* ===================================================
       VERIFICAR OUTRO RENDIMENTO NO MÊS
       DURANTE UMA EDIÇÃO
    =================================================== */

    @Query("""
        SELECT CASE
            WHEN COUNT(m) > 0
                THEN true
            ELSE false
        END
        FROM InvestimentoMovimentacao m
        WHERE m.investimento.id = :investimentoId
        AND m.tipo =
            TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento.RENDIMENTO
        AND m.id <> :movimentacaoId
        AND m.data >= :inicio
        AND m.data < :fim
    """)
    boolean existeOutroRendimentoNoPeriodo(

            @Param("investimentoId")
            Long investimentoId,

            @Param("movimentacaoId")
            Long movimentacaoId,

            @Param("inicio")
            LocalDateTime inicio,

            @Param("fim")
            LocalDateTime fim

    );

}