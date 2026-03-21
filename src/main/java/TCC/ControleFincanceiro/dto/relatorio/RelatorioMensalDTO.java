package TCC.ControleFincanceiro.dto.relatorio;

import java.math.BigDecimal;

public record RelatorioMensalDTO(
        String mes,
        BigDecimal receita,
        BigDecimal despesa,
        BigDecimal investimento
) {}