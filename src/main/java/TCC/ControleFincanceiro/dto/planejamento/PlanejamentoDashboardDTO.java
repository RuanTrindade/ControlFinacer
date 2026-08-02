package TCC.ControleFincanceiro.dto.planejamento;

import java.util.List;

public record PlanejamentoDashboardDTO(

        PlanejamentoMensalResumoDTO resumoMensal,

        List<PlanejamentoCategoriaResumoDTO> categorias

) {
}