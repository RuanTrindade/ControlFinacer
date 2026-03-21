package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;

public record PlanejamentoAtualizarDTO(
        Long usuarioId,
        BigDecimal rendaMensal,
        BigDecimal percentualEconomia
) {}
