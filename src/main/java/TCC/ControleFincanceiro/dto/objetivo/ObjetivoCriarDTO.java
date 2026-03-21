package TCC.ControleFincanceiro.dto.objetivo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoCriarDTO(
        Long usuarioId,
        String nome,
        BigDecimal valorObjetivo,
        LocalDate dataFinal,
        String cor,
        String icone
) {}