package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record IndicadoresIADTO(
        BigDecimal saldo,
        BigDecimal receitaMes,
        BigDecimal despesaMes,
        BigDecimal investimentoMes,
        BigDecimal carteiraInvestimentos
) {
}