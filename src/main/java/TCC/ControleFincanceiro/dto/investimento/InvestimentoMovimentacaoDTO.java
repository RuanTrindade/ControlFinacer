package TCC.ControleFincanceiro.dto.investimento;

import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestimentoMovimentacaoDTO(

        Long id,
        String nomeInvestimento,
        TipoInvestimento tipo,
        BigDecimal valor,
        LocalDateTime data

) {
}