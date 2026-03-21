package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;

public record PlanejamentoCategoriaDTO(
        Long planejamentoId,
        Long categoriaId,
        BigDecimal limite
) {}