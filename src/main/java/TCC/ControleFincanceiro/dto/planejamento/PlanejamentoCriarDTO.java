package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanejamentoCriarDTO(
        Long usuarioId,
        LocalDate referencia,
        BigDecimal rendaMensal,
        BigDecimal percentualEconomia
) {}