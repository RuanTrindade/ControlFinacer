package TCC.ControleFincanceiro.dto.investimento;

import java.math.BigDecimal;

public record InvestimentoEstatisticaDTO(
        Long id, String nome,
        BigDecimal totalInvestido,
        BigDecimal totalResgatado,
        BigDecimal totalRendimento,
        BigDecimal saldoAtual
) {}