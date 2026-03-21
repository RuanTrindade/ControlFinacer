package TCC.ControleFincanceiro.dto.objetivo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoResumoDTO(
        Long id,
        String nome,
        BigDecimal valorObjetivo,
        BigDecimal valorAtual,
        BigDecimal percentual,
        LocalDate dataFinal,
        Boolean finalizado
) {}