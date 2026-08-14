package TCC.ControleFincanceiro.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardTransacaoDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        String tipo,
        String categoria,
        String icone
) {
}