package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record ComparacaoMensalIADTO(
        BigDecimal receitaVariacao,
        BigDecimal despesaVariacao,
        BigDecimal investimentoVariacao,
        String mensagem
) {
}