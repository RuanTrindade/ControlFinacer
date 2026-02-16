package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.ObjetivoDeposito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjetivoDepositoRepository extends JpaRepository<ObjetivoDeposito, Long> {

    List<ObjetivoDeposito> findByObjetivoId(Long objetivoId);
}
