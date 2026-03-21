package TCC.ControleFincanceiro.dto.objetivo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoAtualizarDTO(
        String nome,
        BigDecimal valorObjetivo,
        LocalDate dataFinal,
        String cor,
        String icone
) {}