package TCC.ControleFincanceiro.dto.ia;

import java.util.List;

public record InterpretacaoIAInternoDTO(
        String nivelFinanceiro,
        List<AlertaIAInternoDTO> alertas,
        List<RedistribuicaoIAInternoDTO> redistribuicoes,
        List<SugestaoIAInternoDTO> sugestoes,
        List<String> insights
) {
}