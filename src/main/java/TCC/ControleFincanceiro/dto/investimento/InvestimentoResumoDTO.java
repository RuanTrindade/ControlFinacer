package TCC.ControleFincanceiro.dto.investimento;


import java.math.BigDecimal;
import java.util.List;


public record InvestimentoResumoDTO(
        BigDecimal totalInvestido,
        BigDecimal totalRendimentos,
        List<InvestimentoCardDTO> investimentos
) {}
