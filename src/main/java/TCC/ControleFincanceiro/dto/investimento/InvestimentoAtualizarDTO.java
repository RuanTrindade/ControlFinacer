package TCC.ControleFincanceiro.dto.investimento;

import java.math.BigDecimal;

public record InvestimentoAtualizarDTO(
        String nome,
        String tipo,
        BigDecimal taxaAtual
) {}