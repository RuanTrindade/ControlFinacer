package TCC.ControleFincanceiro.dto.ia;

import java.util.List;

public record AnaliseIADTO(
        String resumo,
        String nivelFinanceiro,
        IndicadoresIADTO indicadores,
        ComparacaoMensalIADTO comparacaoMensal,
        List<AlertaIAInternoDTO> alertas,
        List<RedistribuicaoIAInternoDTO> redistribuicoes,
        List<SugestaoIAInternoDTO> sugestoes,
        List<ObjetivoIADTO> objetivos,
        InvestimentoIADTO investimentos,
        List<String> insights,
        List<HistoricoIADTO> historico

) {
}