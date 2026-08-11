package TCC.ControleFincanceiro.dto.investimento;

import java.math.BigDecimal;

public record CarteiraInvestimentoResumoDTO(
        BigDecimal valorCarteiraNoMes,
        BigDecimal aportesMes,
        BigDecimal resgatesMes,
        BigDecimal rendimentoMes,
        Long investimentosAtivos,
        Long investimentosPendentesAtualizacao

) {
}