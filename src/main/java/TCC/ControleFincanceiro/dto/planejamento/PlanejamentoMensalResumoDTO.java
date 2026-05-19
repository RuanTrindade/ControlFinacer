package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;

public record PlanejamentoMensalResumoDTO(
        Long id, BigDecimal rendaMensal,
        BigDecimal gastoTotal,
        BigDecimal economizado,
        BigDecimal percentualGasto,
        BigDecimal percentualEconomizado
) {}