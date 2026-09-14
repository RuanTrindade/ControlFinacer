package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.dashboard.DashboardDTO;
import TCC.ControleFincanceiro.dto.ia.*;
import TCC.ControleFincanceiro.dto.investimento.CarteiraInvestimentoResumoDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoResumoDTO;
import TCC.ControleFincanceiro.dto.relatorio.RelatorioCategoriaDTO;
import TCC.ControleFincanceiro.dto.relatorio.RelatorioDTO;
import TCC.ControleFincanceiro.dto.relatorio.RelatorioMensalDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnaliseIAService {

    private final DashboardService dashboardService;
    private final RelatorioService relatorioService;
    private final InvestimentoService investimentoService;
    private final ObjetivoService objetivoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnaliseIADTO analisar(Long usuarioId, int mes, int ano) {

        DashboardDTO dashboard =
                dashboardService.buscarDashboard(usuarioId, mes, ano);

        RelatorioDTO relatorio =
                relatorioService.gerarRelatorio(usuarioId, mes, ano);

        CarteiraInvestimentoResumoDTO carteira =
                investimentoService.obterResumoCarteira(usuarioId, mes, ano);

        List<ObjetivoResumoDTO> objetivosAtivos =
                objetivoService.listar(usuarioId, false, Pageable.unpaged())
                        .getContent();

        IndicadoresIADTO indicadores = new IndicadoresIADTO(
                dashboard.saldo(),
                dashboard.receita(),
                dashboard.despesa(),
                dashboard.investimento(),
                carteira.valorCarteiraNoMes()
        );

        List<HistoricoIADTO> historico = relatorio.historico()
                .stream()
                .map(item -> new HistoricoIADTO(
                        item.mes(),
                        item.receita(),
                        item.despesa(),
                        item.investimento()
                ))
                .toList();

        ComparacaoMensalIADTO comparacao =
                montarComparacao(relatorio.historico());

        List<ObjetivoIADTO> objetivos = objetivosAtivos.stream()
                .map(objetivo -> new ObjetivoIADTO(
                        objetivo.id(),
                        objetivo.nome(),
                        objetivo.valorObjetivo(),
                        objetivo.valorAtual(),
                        objetivo.percentual(),
                        objetivo.dataFinal(),
                        recomendacaoObjetivo(objetivo)
                ))
                .toList();

        InvestimentoIADTO investimentos = new InvestimentoIADTO(
                carteira.valorCarteiraNoMes(),
                carteira.aportesMes(),
                carteira.resgatesMes(),
                carteira.rendimentoMes(),
                carteira.investimentosAtivos(),
                mensagemInvestimentos(carteira)
        );

        String contexto = montarContexto(
                mes,
                ano,
                dashboard,
                relatorio,
                carteira,
                objetivosAtivos,
                comparacao
        );

        InterpretacaoIAInternoDTO interpretacao =
                gerarInterpretacao(contexto);

        String resumo =
                gerarResumo(contexto, interpretacao);

        return new AnaliseIADTO(
                resumo,
                normalizarNivel(interpretacao.nivelFinanceiro()),
                indicadores,
                comparacao,
                listaOuVazia(interpretacao.alertas()),
                listaOuVazia(interpretacao.redistribuicoes()),
                listaOuVazia(interpretacao.sugestoes()),
                objetivos,
                investimentos,
                listaOuVazia(interpretacao.insights()),
                historico
        );
    }



    private String montarContexto(
            int mes,
            int ano,
            DashboardDTO dashboard,
            RelatorioDTO relatorio,
            CarteiraInvestimentoResumoDTO carteira,
            List<ObjetivoResumoDTO> objetivos,
            ComparacaoMensalIADTO comparacao
    ) {

        StringBuilder contexto = new StringBuilder();

        contexto.append("PERIODO: %02d/%d\n\n".formatted(mes, ano));

        contexto.append("FINANCEIRO:\n")
                .append("- Saldo acumulado: R$ ").append(valor(dashboard.saldo())).append("\n")
                .append("- Receita registrada no mês: R$ ").append(valor(dashboard.receita())).append("\n")
                .append("- Despesas pagas no mês: R$ ").append(valor(dashboard.despesa())).append("\n")
                .append("- Aportes em investimentos no mês: R$ ").append(valor(dashboard.investimento())).append("\n\n");

        contexto.append("INVESTIMENTOS:\n")
                .append("- Carteira atual até o período: R$ ").append(valor(carteira.valorCarteiraNoMes())).append("\n")
                .append("- Aportes no mês: R$ ").append(valor(carteira.aportesMes())).append("\n")
                .append("- Resgates no mês: R$ ").append(valor(carteira.resgatesMes())).append("\n")
                .append("- Rendimentos acumulados até o período: R$ ").append(valor(carteira.rendimentoMes())).append("\n")
                .append("- Investimentos ativos: ").append(carteira.investimentosAtivos()).append("\n\n");

        contexto.append("PLANEJAMENTO:\n");

        if (relatorio.categoriasPlanejamento().isEmpty()) {

            contexto.append("- Nenhum planejamento criado neste mês.\n");

        } else {

            for (RelatorioCategoriaDTO categoria : relatorio.categoriasPlanejamento()) {

                BigDecimal restante =
                        categoria.limite().subtract(categoria.utilizado());

                contexto.append("- ")
                        .append(categoria.nome())
                        .append(": limite R$ ")
                        .append(valor(categoria.limite()))
                        .append(", utilizado R$ ")
                        .append(valor(categoria.utilizado()))
                        .append(", percentual ")
                        .append(valor(categoria.percentualUtilizado()))
                        .append("%");

                if (restante.compareTo(BigDecimal.ZERO) > 0) {
                    contexto.append(", disponível R$ ").append(valor(restante));

                } else if (restante.compareTo(BigDecimal.ZERO) < 0) {
                    contexto.append(", excesso R$ ").append(valor(restante.abs()));

                } else {
                    contexto.append(", limite atingido");
                }

                contexto.append("\n");
            }
        }

        contexto.append("\nOBJETIVOS:\n");

        if (objetivos.isEmpty()) {

            contexto.append("- Nenhum objetivo ativo.\n");

        } else {

            for (ObjetivoResumoDTO objetivo : objetivos) {

                contexto.append("- ")
                        .append(objetivo.nome())
                        .append(": meta R$ ")
                        .append(valor(objetivo.valorObjetivo()))
                        .append(", valor atual R$ ")
                        .append(valor(objetivo.valorAtual()))
                        .append(", progresso ")
                        .append(valor(objetivo.percentual()))
                        .append("%");

                if (objetivo.dataFinal() != null) {
                    contexto.append(", prazo ").append(objetivo.dataFinal());
                }

                contexto.append("\n");
            }
        }

        contexto.append("\nHISTORICO DOS ULTIMOS 6 MESES:\n");

        for (RelatorioMensalDTO item : relatorio.historico()) {

            contexto.append("- ")
                    .append(item.mes())
                    .append(": receita R$ ")
                    .append(valor(item.receita()))
                    .append(", despesa R$ ")
                    .append(valor(item.despesa()))
                    .append(", investimento R$ ")
                    .append(valor(item.investimento()))
                    .append("\n");
        }

        contexto.append("\nCOMPARACAO COM MES ANTERIOR:\n")
                .append("- Receita: ").append(valor(comparacao.receitaVariacao())).append("%\n")
                .append("- Despesa: ").append(valor(comparacao.despesaVariacao())).append("%\n")
                .append("- Investimento: ").append(valor(comparacao.investimentoVariacao())).append("%\n")
                .append("- ").append(comparacao.mensagem());

        return contexto.toString();
    }



    private InterpretacaoIAInternoDTO gerarInterpretacao(String contexto) {

        String prompt = """
                Você é um assistente de análise financeira pessoal.

                Analise SOMENTE os dados fornecidos pelo sistema.

                NÃO invente valores.
                NÃO invente categorias.
                NÃO invente objetivos.

                NÃO diga que o usuário não possui renda apenas porque
                a receita do período está zerada.
                Diga somente que não há receita registrada no período.

                Se uma categoria planejada estiver com pouca utilização
                e outra estiver próxima ou acima do limite,
                você pode sugerir redistribuição do orçamento.

                Não sugira valor maior que o disponível na categoria de origem.

                Se não houver planejamento ou redistribuição coerente,
                use "redistribuicoes": [].

                Responda SOMENTE JSON válido.
                Não use markdown.
                Não escreva texto antes ou depois do JSON.

                Use EXATAMENTE:

                {
                  "nivelFinanceiro": "BOM",
                  "alertas": [
                    {
                      "tipo": "PLANEJAMENTO",
                      "categoria": "",
                      "severidade": "ATENCAO",
                      "mensagem": ""
                    }
                  ],
                  "redistribuicoes": [
                    {
                      "origem": "",
                      "destino": "",
                      "valorSugerido": 0,
                      "motivo": ""
                    }
                  ],
                  "sugestoes": [
                    {
                      "titulo": "",
                      "descricao": "",
                      "prioridade": "MEDIA"
                    }
                  ],
                  "insights": [""]
                }

                nivelFinanceiro:
                MUITO_BOM
                BOM
                ATENCAO
                CRITICO

                severidade:
                INFO
                ATENCAO
                CRITICO

                prioridade:
                BAIXA
                MEDIA
                ALTA

                tipo:
                RECEITA
                DESPESA
                INVESTIMENTO
                PLANEJAMENTO
                OBJETIVO
                GERAL

                Máximo:
                4 alertas
                3 redistribuições
                5 sugestões
                5 insights

                DADOS:

                """ + contexto;

        try {

            String resposta = chamarIA(prompt, true);
            String json = extrairJson(resposta);

            InterpretacaoIAInternoDTO dto =
                    objectMapper.readValue(
                            json,
                            InterpretacaoIAInternoDTO.class
                    );

            return normalizarInterpretacao(dto);

        } catch (Exception e) {

            System.err.println(
                    "Erro ao interpretar JSON da IA: " + e.getMessage()
            );

            return interpretacaoFallback();
        }
    }



    private String gerarResumo(
            String contexto,
            InterpretacaoIAInternoDTO interpretacao
    ) {

        String prompt = """
                Você é um assistente financeiro pessoal.

                Produza um resumo financeiro personalizado
                com base SOMENTE nos dados abaixo.

                REGRAS:

                - português do Brasil
                - máximo 3 parágrafos
                - linguagem simples
                - não use JSON
                - não use markdown
                - não invente números
                - não invente datas ou acontecimentos
                - não faça novos cálculos
                - se um valor estiver zerado, diga apenas
                  que não há registro daquele valor no período
                - não conclua que o usuário precisa aumentar renda,
                  a menos que os dados provem isso
                - cite pontos positivos quando existirem
                - cite problemas relevantes quando existirem
                - finalize com uma recomendação prática

                DADOS:

                """ + contexto
                + "\n\nNIVEL FINANCEIRO: "
                + normalizarNivel(interpretacao.nivelFinanceiro());

        try {

            String resposta = chamarIA(prompt, false);

            if (resposta == null || resposta.isBlank()) {
                return resumoFallback();
            }

            return resposta.replace("```", "").trim();

        } catch (Exception e) {

            System.err.println(
                    "Erro ao gerar resumo da IA: " + e.getMessage()
            );

            return resumoFallback();
        }
    }



    private String chamarIA(String prompt, boolean json) {

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> opcoes = Map.of(
                "temperature",
                json ? 0.1 : 0.4
        );

        Map<String, Object> body;

        if (json) {

            body = Map.of(
                    "model", "llama3",
                    "prompt", prompt,
                    "stream", false,
                    "format", "json",
                    "options", opcoes
            );

        } else {

            body = Map.of(
                    "model", "llama3",
                    "prompt", prompt,
                    "stream", false,
                    "options", opcoes
            );
        }

        Map<?, ?> resposta = restTemplate.postForObject(
                "http://localhost:11434/api/generate",
                body,
                Map.class
        );

        if (resposta == null || resposta.get("response") == null) {
            throw new RuntimeException("O Ollama não retornou resposta.");
        }

        return resposta.get("response").toString();
    }



    private ComparacaoMensalIADTO montarComparacao(
            List<RelatorioMensalDTO> historico
    ) {

        if (historico == null || historico.size() < 2) {

            return new ComparacaoMensalIADTO(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    "Não há histórico suficiente para comparação."
            );
        }

        RelatorioMensalDTO atual =
                historico.get(historico.size() - 1);

        RelatorioMensalDTO anterior =
                historico.get(historico.size() - 2);

        return new ComparacaoMensalIADTO(
                calcularVariacao(anterior.receita(), atual.receita()),
                calcularVariacao(anterior.despesa(), atual.despesa()),
                calcularVariacao(anterior.investimento(), atual.investimento()),
                "Comparação entre "
                        + anterior.mes()
                        + " e "
                        + atual.mes()
                        + "."
        );
    }

    private BigDecimal calcularVariacao(
            BigDecimal anterior,
            BigDecimal atual
    ) {

        anterior = valorOuZero(anterior);
        atual = valorOuZero(atual);

        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return atual
                .subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }



    private String recomendacaoObjetivo(
            ObjetivoResumoDTO objetivo
    ) {

        BigDecimal percentual =
                valorOuZero(objetivo.percentual());

        if (percentual.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "Meta financeira atingida.";
        }

        if (percentual.compareTo(BigDecimal.valueOf(75)) >= 0) {
            return "O objetivo está próximo da conclusão.";
        }

        if (percentual.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "O objetivo apresenta bom progresso.";
        }

        return "O objetivo ainda está no início e exige continuidade nos depósitos.";
    }



    private String mensagemInvestimentos(
            CarteiraInvestimentoResumoDTO carteira
    ) {

        if (carteira.investimentosAtivos() == 0) {
            return "Não existem investimentos ativos neste período.";
        }

        if (carteira.aportesMes().compareTo(BigDecimal.ZERO) > 0) {
            return "Houve aportes em investimentos neste mês.";
        }

        return "Existem investimentos ativos, mas não houve novos aportes neste mês.";
    }



    private InterpretacaoIAInternoDTO normalizarInterpretacao(
            InterpretacaoIAInternoDTO dto
    ) {

        if (dto == null) {
            return interpretacaoFallback();
        }

        return new InterpretacaoIAInternoDTO(
                normalizarNivel(dto.nivelFinanceiro()),
                listaOuVazia(dto.alertas()),
                listaOuVazia(dto.redistribuicoes()),
                listaOuVazia(dto.sugestoes()),
                listaOuVazia(dto.insights())
        );
    }

    private InterpretacaoIAInternoDTO interpretacaoFallback() {

        return new InterpretacaoIAInternoDTO(
                "INDEFINIDO",
                List.of(),
                List.of(),
                List.of(
                        new SugestaoIAInternoDTO(
                                "Continue acompanhando",
                                "Mantenha seus registros financeiros atualizados para melhorar as análises.",
                                "BAIXA"
                        )
                ),
                List.of(
                        "A análise automática detalhada não pôde ser concluída."
                )
        );
    }

    private String resumoFallback() {

        return """
                Os dados financeiros foram carregados, mas a análise textual
                da inteligência artificial não pôde ser concluída neste momento.
                """.trim();
    }

    private String normalizarNivel(String nivel) {

        if (nivel == null) {
            return "INDEFINIDO";
        }

        return switch (nivel.trim().toUpperCase()) {
            case "MUITO_BOM" -> "MUITO_BOM";
            case "BOM" -> "BOM";
            case "ATENCAO" -> "ATENCAO";
            case "CRITICO" -> "CRITICO";
            default -> "INDEFINIDO";
        };
    }



    private String extrairJson(String resposta) {

        int inicio = resposta.indexOf("{");
        int fim = resposta.lastIndexOf("}");

        if (inicio < 0 || fim < inicio) {
            throw new RuntimeException("JSON inválido.");
        }

        return resposta.substring(inicio, fim + 1);
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String valor(BigDecimal valor) {
        return valorOuZero(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private <T> List<T> listaOuVazia(List<T> lista) {
        return lista != null ? lista : List.of();
    }
}