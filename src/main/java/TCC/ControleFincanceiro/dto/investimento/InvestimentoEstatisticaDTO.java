package TCC.ControleFincanceiro.dto.investimento;

import java.math.BigDecimal;

public record InvestimentoEstatisticaDTO(

        Long id,
        String nome,
        BigDecimal totalAportado,
        BigDecimal totalResgatado,
        BigDecimal rendimento,
        BigDecimal saldoAtual

) {
}