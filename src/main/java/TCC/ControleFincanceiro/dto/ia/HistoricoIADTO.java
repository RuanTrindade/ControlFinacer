package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record HistoricoIADTO(
        String mes,
        BigDecimal receita,
        BigDecimal despesa,
        BigDecimal investimento
) {
}