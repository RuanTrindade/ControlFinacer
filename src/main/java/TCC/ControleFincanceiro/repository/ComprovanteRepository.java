package TCC.ControleFincanceiro.repository;

import TCC.ControleFincanceiro.entity.Comprovante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprovanteRepository extends JpaRepository<Comprovante, Long> {
}