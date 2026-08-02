package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;

public record PlanejamentoValorAtualizarDTO(

        Long usuarioId,

        BigDecimal valorPlanejado

) {
}