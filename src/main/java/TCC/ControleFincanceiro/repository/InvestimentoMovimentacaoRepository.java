package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.InvestimentoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvestimentoMovimentacaoRepository extends JpaRepository<InvestimentoMovimentacao, Long> {

    List<InvestimentoMovimentacao> findByInvestimentoIdOrderByDataAsc(Long investimentoId);

    @Query("""
       SELECT COALESCE(SUM(
           CASE 
               WHEN m.tipo IN ('APORTE','RENDIMENTO') THEN m.valor
               WHEN m.tipo = 'RESGATE' THEN -m.valor
           END
       ), 0)
       FROM InvestimentoMovimentacao m
       WHERE m.investimento.id = :investimentoId
       """)
    BigDecimal calcularSaldo(Long investimentoId);

    @Query("""
       SELECT COALESCE(SUM(
           CASE 
               WHEN m.tipo IN ('APORTE','RENDIMENTO') THEN m.valor
               WHEN m.tipo = 'RESGATE' THEN -m.valor
           END
       ), 0)
       FROM InvestimentoMovimentacao m
       WHERE m.investimento.usuario.id = :usuarioId
       """)
    BigDecimal calcularSaldoTotalUsuario(Long usuarioId);

    @Query("""
    SELECT COALESCE(SUM(m.valor), 0)
    FROM InvestimentoMovimentacao m
    WHERE m.investimento.id = :investimentoId
    AND m.tipo = 'RENDIMENTO'
""")
    BigDecimal totalRendimento(Long investimentoId);

    @Query("""
    SELECT COALESCE(SUM(m.valor), 0)
    FROM InvestimentoMovimentacao m
    WHERE m.investimento.id = :investimentoId
    AND m.tipo = 'APORTE'
""")
    BigDecimal totalAportes(Long investimentoId);

    @Query("""
    SELECT COALESCE(SUM(m.valor), 0)
    FROM InvestimentoMovimentacao m
    WHERE m.investimento.id = :investimentoId
    AND m.tipo = 'RESGATE'
""")
    BigDecimal totalResgates(Long investimentoId);

    Optional<InvestimentoMovimentacao> findByTransacaoId(Long transacaoId);

}
