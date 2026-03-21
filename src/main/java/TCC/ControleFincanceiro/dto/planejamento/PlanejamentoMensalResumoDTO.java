package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;

public record PlanejamentoMensalResumoDTO(
        BigDecimal rendaMensal,
        BigDecimal gastoTotal,
        BigDecimal economizado,
        BigDecimal percentualGasto,
        BigDecimal percentualEconomizado
) {}