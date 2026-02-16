package TCC.ControleFincanceiro.dto.investimento;


import java.math.BigDecimal;



public record InvestimentoCardDTO(
        Long id,
        String nome,
        BigDecimal saldo,
        BigDecimal taxaAtual
) {}
