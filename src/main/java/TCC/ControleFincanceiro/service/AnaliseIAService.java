package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.ia.AnaliseIADTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResumoDTO;
import TCC.ControleFincanceiro.entity.Objetivo;
import TCC.ControleFincanceiro.repository.InvestimentoMovimentacaoRepository;
import TCC.ControleFincanceiro.repository.ObjetivoRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnaliseIAService {

    private final TransacaoRepository transacaoRepository;
    private final PlanejamentoCategoriaService planejamentoCategoriaService;
    private final ObjetivoRepository objetivoRepository;
    private final InvestimentoMovimentacaoRepository investimentoRepository;

    public AnaliseIADTO analisar(Long usuarioId) {



        BigDecimal receita = transacaoRepository.totalReceitas(usuarioId);
        BigDecimal despesas = transacaoRepository.totalDespesas(usuarioId);
        BigDecimal investimentos = investimentoRepository.calcularSaldoTotalUsuario(usuarioId);

        List<PlanejamentoCategoriaResumoDTO> categorias =
                planejamentoCategoriaService.resumoPorUsuario(usuarioId);

        List<Objetivo> objetivos = objetivoRepository.findByUsuarioId(usuarioId);



        StringBuilder contexto = new StringBuilder();

        contexto.append("DADOS FINANCEIROS DO USUÁRIO:\n");
        contexto.append("Receita total: ").append(receita).append("\n");
        contexto.append("Despesas totais: ").append(despesas).append("\n");
        contexto.append("Investimentos: ").append(investimentos).append("\n\n");

        contexto.append("CATEGORIAS:\n");

        for (PlanejamentoCategoriaResumoDTO c : categorias) {
            contexto.append("- ")
                    .append(c.categoria())
                    .append(": Planejado ")
                    .append(c.planejado())
                    .append(", Gasto ")
                    .append(c.gasto())
                    .append("\n");
        }

        contexto.append("\nOBJETIVOS:\n");

        for (Objetivo o : objetivos) {
            contexto.append("- ")
                    .append(o.getNome())
                    .append(": meta ")
                    .append(o.getValorObjetivo())
                    .append("\n");
        }



        String prompt = """
Você é um especialista em educação financeira pessoal.

Analise os dados do usuário abaixo e gere uma resposta em JSON com os seguintes campos:

{
  "resumo": "resumo geral da situação financeira",
  "pontosAtencao": ["lista de problemas encontrados"],
  "sugestoes": ["lista de sugestões práticas"],
  "previsao": "projeção futura baseada no comportamento atual",
  "nivelFinanceiro": "iniciante, intermediário ou avançado"
}

REGRAS:
- Seja direto e claro
- Use linguagem simples
- Dê sugestões práticas
- Identifique excessos de gasto
- Considere objetivos do usuário

DADOS:
""" + contexto;

        // ==============================
        // 4. CHAMAR IA
        // ==============================

        String resposta = chamarIA(prompt);

        // ==============================
        // 5. CONVERTER JSON → DTO
        // ==============================

        return converterParaDTO(resposta);
    }

    // ==============================
    // MOCK (substituir pela API real)
    // ==============================
    private String chamarIA(String prompt) {

        System.out.println(prompt); // debug

        return """
        {
          "resumo": "Você está gastando mais do que deveria em algumas categorias.",
          "pontosAtencao": ["Gasto alto em alimentação"],
          "sugestoes": ["Reduzir gastos em 20% nessa categoria"],
          "previsao": "Você pode economizar R$500/mês",
          "nivelFinanceiro": "intermediario"
        }
        """;
    }

    private AnaliseIADTO converterParaDTO(String json) {
        // aqui você pode usar ObjectMapper depois
        return new AnaliseIADTO(
                "Resumo exemplo",
                List.of("Ponto 1"),
                List.of("Sugestão 1"),
                "Previsão exemplo",
                "Intermediário"
        );
    }
}