package TCC.ControleFincanceiro.dto.investimento;

import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;

import java.math.BigDecimal;

public record InvestimentoMovimentacaoRequestDTO(
        BigDecimal valor,
        TipoInvestimento tipo
) {
}