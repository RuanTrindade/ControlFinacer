package TCC.ControleFincanceiro.dto.objetivo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoMovimentacaoDTO(

        Long id,
        String nomeObjetivo,
        String tipo,
        BigDecimal valor,
        LocalDate data

) {
}