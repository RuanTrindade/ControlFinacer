package TCC.ControleFincanceiro.dto.ia;

import java.util.List;

public record AnaliseIADTO(
        String resumo,
        String nivelFinanceiro,
        List<AlertaDTO> alertas,
        List<SugestaoDTO> sugestoes,
        List<String> insights,
        PrevisaoDTO previsao
) {}