package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record SugestaoDTO(
        String descricao,
        BigDecimal economiaPotencial
) {}