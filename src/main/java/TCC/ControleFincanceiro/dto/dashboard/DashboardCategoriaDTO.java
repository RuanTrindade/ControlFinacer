package TCC.ControleFincanceiro.dto.dashboard;

import java.math.BigDecimal;

public record DashboardCategoriaDTO(
        Long categoriaId,
        String nome,
        String icone,
        BigDecimal limite,
        BigDecimal utilizado,
        BigDecimal percentualUtilizado,
        String status
) {
}