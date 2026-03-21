package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.ia.*;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResumoDTO;
import TCC.ControleFincanceiro.entity.Objetivo;
import TCC.ControleFincanceiro.repository.InvestimentoMovimentacaoRepository;
import TCC.ControleFincanceiro.repository.ObjetivoRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnaliseIAService {

    private final TransacaoRepository transacaoRepository;
    private final PlanejamentoCategoriaService planejamentoCategoriaService;
    private final ObjetivoRepository objetivoRepository;
    private final InvestimentoMovimentacaoRepository investimentoRepository;

    // ==============================
    // MÉTODO PRINCIPAL
    // ==============================
    public AnaliseIADTO analisar(Long usuarioId) {

        BigDecimal receita = transacaoRepository.totalReceitas(usuarioId);
        BigDecimal despesas = transacaoRepository.totalDespesas(usuarioId);
        BigDecimal investimentos = investimentoRepository.calcularSaldoTotalUsuario(usuarioId);

        List<PlanejamentoCategoriaResumoDTO> categorias =
                planejamentoCategoriaService.resumoPorUsuario(usuarioId);

        List<Objetivo> objetivos = objetivoRepository.findByUsuarioId(usuarioId);

        StringBuilder contexto = new StringBuilder();

        contexto.append("Receita: ").append(receita).append("\n");
        contexto.append("Despesas: ").append(despesas).append("\n");
        contexto.append("Investimentos: ").append(investimentos).append("\n\n");

        contexto.append("CATEGORIAS:\n");

        for (PlanejamentoCategoriaResumoDTO c : categorias) {
            BigDecimal diff = c.gasto().subtract(c.planejado());

            contexto.append("- ")
                    .append(c.categoria())
                    .append(": limite ")
                    .append(c.planejado())
                    .append(", gasto ")
                    .append(c.gasto())
                    .append(", diferenca ")
                    .append(diff)
                    .append(diff.compareTo(BigDecimal.ZERO) < 0
                            ? " (economia de R$" + diff.abs() + ")"
                            : " (excesso de R$" + diff + ")")
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
Você é um consultor financeiro.

Responda APENAS em JSON válido, seguindo EXATAMENTE esta estrutura:

{
  "resumo": "",
  "nivelFinanceiro": "",
  "alertas": [
    {
      "categoria": "",
      "gasto": 0,
      "limite": 0,
      "excesso": 0,
      "mensagem": ""
    }
  ],
  "sugestoes": [
    {
      "descricao": "",
      "economiaPotencial": 0
    }
  ],
  "insights": [
    ""
  ],
  "previsao": {
    "economiaMensal": 0,
    "tempoParaObjetivo": ""
  }
}

REGRAS OBRIGATÓRIAS:

- Use exatamente os nomes dos campos acima
- NÃO use "diferenca", use "excesso"
- NÃO crie novos campos
- NÃO mude a estrutura
- insights deve ser lista de strings (não objetos)
- alertas deve conter mensagem obrigatoriamente

DADOS:
""" + contexto;

        String resposta = chamarIA(prompt);

        System.out.println("=== RESPOSTA IA ===");
        System.out.println(resposta);

        String jsonLimpo;
        try {
            jsonLimpo = extrairJson(resposta);
        } catch (Exception e) {
            return respostaErro("Erro ao interpretar resposta da IA.");
        }

        AnaliseIADTO dto;
        try {
            dto = converterParaDTO(jsonLimpo);
        } catch (Exception e) {
            return respostaErro("Erro ao converter resposta da IA.");
        }

        return completarCampos(dto);
    }

    // ==============================
    // CHAMADA IA (OLLAMA)
    // ==============================
    private String chamarIA(String prompt) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:11434/api/generate";

        Map<String, Object> body = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        Map response = restTemplate.postForObject(url, body, Map.class);

        return (String) response.get("response");
    }

    // ==============================
    // EXTRAIR JSON
    // ==============================
    private String extrairJson(String resposta) {

        int inicio = resposta.indexOf("{");
        int fim = resposta.lastIndexOf("}");

        if (inicio == -1 || fim == -1) {
            throw new RuntimeException("Sem JSON válido");
        }

        return resposta.substring(inicio, fim + 1);
    }

    // ==============================
    // CONVERTER JSON → DTO
    // ==============================
    private AnaliseIADTO converterParaDTO(String json) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, AnaliseIADTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro JSON: " + json);
        }
    }

    // ==============================
    // COMPLETAR CAMPOS (SEM FORÇAR)
    // ==============================
    private AnaliseIADTO completarCampos(AnaliseIADTO dto) {

        String resumo = (dto.resumo() == null || dto.resumo().isBlank())
                ? "Resumo não gerado pela IA."
                : dto.resumo();

        String nivel = (dto.nivelFinanceiro() == null || dto.nivelFinanceiro().isBlank())
                ? "indefinido"
                : dto.nivelFinanceiro();

        List<String> insights = (dto.insights() == null || dto.insights().isEmpty())
                ? List.of("Sem insights gerados.")
                : dto.insights();

        PrevisaoDTO previsao = dto.previsao();
        if (previsao == null) {
            previsao = new PrevisaoDTO(BigDecimal.ZERO, "não definido");
        } else if (previsao.tempoParaObjetivo() == null || previsao.tempoParaObjetivo().isBlank()) {
            previsao = new PrevisaoDTO(
                    previsao.economiaMensal() != null ? previsao.economiaMensal() : BigDecimal.ZERO,
                    "não definido"
            );
        }

        return new AnaliseIADTO(
                resumo,
                nivel,
                dto.alertas(),
                dto.sugestoes(),
                insights,
                previsao
        );
    }

    // ==============================
    // ERRO CONTROLADO (NUNCA NULL)
    // ==============================
    private AnaliseIADTO respostaErro(String mensagem) {

        return new AnaliseIADTO(
                mensagem,
                "indefinido",
                List.of(),
                List.of(),
                List.of("Não foi possível analisar os dados."),
                new PrevisaoDTO(BigDecimal.ZERO, "não definido")
        );
    }
}