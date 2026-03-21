package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanejamentoResumoDTO(
        Long id,
        LocalDate referencia,
        BigDecimal rendaMensal,
        BigDecimal percentualEconomia
) {}