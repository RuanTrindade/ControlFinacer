package TCC.ControleFincanceiro.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardDTO(
        BigDecimal saldo,
        BigDecimal receita,
        BigDecimal despesa,
        BigDecimal investimento,
        List<DashboardCategoriaDTO> categoriasCriticas,
        List<DashboardTransacaoDTO> ultimasTransacoes

) {
}