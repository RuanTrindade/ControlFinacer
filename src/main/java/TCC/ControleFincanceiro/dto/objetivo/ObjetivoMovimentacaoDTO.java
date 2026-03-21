package TCC.ControleFincanceiro.dto.objetivo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoMovimentacaoDTO(
        String objetivo,
        String tipo,
        BigDecimal valor,
        LocalDate data
) {}