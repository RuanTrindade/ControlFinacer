package TCC.ControleFincanceiro.dto.ia;

import java.util.List;

public record AnaliseIADTO(
        String resumo,
        List<String> pontosAtencao,
        List<String> sugestoes,
        String previsao,
        String nivelFinanceiro
) {}