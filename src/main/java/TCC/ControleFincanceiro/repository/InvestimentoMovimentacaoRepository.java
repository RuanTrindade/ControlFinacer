package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Investimento;
import TCC.ControleFincanceiro.entity.InvestimentoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

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

}
