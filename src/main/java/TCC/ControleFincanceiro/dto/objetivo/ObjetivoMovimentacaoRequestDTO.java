package TCC.ControleFincanceiro.dto.objetivo;

import java.math.BigDecimal;

public record ObjetivoMovimentacaoRequestDTO(
        BigDecimal valor,
        String tipo
) {
}