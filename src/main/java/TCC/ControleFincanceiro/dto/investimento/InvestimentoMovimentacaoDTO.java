package TCC.ControleFincanceiro.dto.investimento;

import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoMovimentacaoDTO(
        String investimento,
        TipoInvestimento tipo,
        BigDecimal valor,
        LocalDate data
) {}