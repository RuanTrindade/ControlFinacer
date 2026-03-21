package TCC.ControleFincanceiro.dto.relatorio;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioDTO(
        List<RelatorioMensalDTO> historico,
        BigDecimal receitaTotal,
        BigDecimal despesaTotal,
        BigDecimal investimentoTotal
) {}