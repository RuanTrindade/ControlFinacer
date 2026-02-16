package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.ObjetivoDeposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ObjetivoDepositoRepository extends JpaRepository<ObjetivoDeposito, Long> {

    List<ObjetivoDeposito> findByObjetivoId(Long objetivoId);

    @Query("""
SELECT COALESCE(SUM(d.valor), 0)
FROM ObjetivoDeposito d 
WHERE d.objetivo.id = :objetivoId
""")
    BigDecimal somarDepositoPorObjetivo(Long objetivoId);

}
