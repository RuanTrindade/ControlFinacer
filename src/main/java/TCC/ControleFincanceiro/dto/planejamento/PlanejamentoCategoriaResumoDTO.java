package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;

public record PlanejamentoCategoriaResumoDTO(
        String categoria,
        BigDecimal planejado,
        BigDecimal gasto,
        String status
) {}