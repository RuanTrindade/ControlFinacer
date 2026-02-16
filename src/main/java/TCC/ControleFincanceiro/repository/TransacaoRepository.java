package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.enumerated.transacao.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.transacao.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

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
}
