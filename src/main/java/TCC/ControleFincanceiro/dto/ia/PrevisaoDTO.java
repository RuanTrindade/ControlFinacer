package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record PrevisaoDTO(
        BigDecimal economiaMensal,
        String tempoParaObjetivo
) {}