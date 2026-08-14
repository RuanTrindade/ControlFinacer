package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoIADTO(
        Long id,
        String nome,
        BigDecimal valorObjetivo,
        BigDecimal valorAtual,
        BigDecimal percentual,
        LocalDate dataFinal,
        String recomendacao
) {
}