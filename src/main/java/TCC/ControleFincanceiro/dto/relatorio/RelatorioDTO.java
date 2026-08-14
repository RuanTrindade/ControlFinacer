package TCC.ControleFincanceiro.dto.relatorio;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioDTO(
        List<RelatorioMensalDTO> historico,
        List<RelatorioCategoriaDTO> categoriasPlanejamento,
        BigDecimal receitaTotal,
        BigDecimal despesaTotal,
        BigDecimal investimentoTotal

) {
}