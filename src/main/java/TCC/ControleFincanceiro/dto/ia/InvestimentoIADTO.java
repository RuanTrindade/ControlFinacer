package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record InvestimentoIADTO(
        BigDecimal valorCarteira,
        BigDecimal aportesMes,
        BigDecimal resgatesMes,
        BigDecimal rendimentoAcumulado,
        long investimentosAtivos,
        String mensagem
) {
}