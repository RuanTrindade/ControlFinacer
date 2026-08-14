package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record RedistribuicaoIAInternoDTO(
        String origem,
        String destino,
        BigDecimal valorSugerido,
        String motivo
) {
}