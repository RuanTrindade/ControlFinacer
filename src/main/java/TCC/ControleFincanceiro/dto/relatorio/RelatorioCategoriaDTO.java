package TCC.ControleFincanceiro.dto.relatorio;

import java.math.BigDecimal;

public record RelatorioCategoriaDTO(
        Long categoriaId,
        String nome,
        String icone,
        String cor,
        BigDecimal limite,
        BigDecimal utilizado,
        BigDecimal percentualUtilizado
) {
}