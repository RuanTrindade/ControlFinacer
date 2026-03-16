package TCC.ControleFincanceiro.dto.investimento;

import TCC.ControleFincanceiro.entity.enumerated.TipoAtivo;

import java.math.BigDecimal;

public record InvestimentoResumoDTO(
        Long id,
        String nome,
        TipoAtivo tipo,
        BigDecimal taxaAtual
) {}