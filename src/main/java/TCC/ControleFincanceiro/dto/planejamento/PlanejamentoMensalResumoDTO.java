package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanejamentoMensalResumoDTO(

        Long id,
        LocalDate referencia,
        BigDecimal rendaMensal,
        BigDecimal receitasMes,
        BigDecimal percentualEconomia,
        BigDecimal metaEconomia,
        BigDecimal valorDisponivel,
        BigDecimal gastoTotal,
        BigDecimal saldoRestante,
        BigDecimal percentualGasto

) {}