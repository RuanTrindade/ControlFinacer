package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.comprovante.ComprovanteResponseDTO;
import TCC.ControleFincanceiro.dto.ia.TransacaoIADTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.Comprovante;
import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.ComprovanteRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComprovanteService {

    private final ComprovanteRepository comprovanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final OcrService ocrService;
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    // ==============================
    // UPLOAD
    // ==============================
    public ComprovanteResponseDTO upload(MultipartFile file, Long usuarioId) {

        try {

            String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path caminho = Paths.get("uploads/" + nomeArquivo);

            Files.createDirectories(caminho.getParent());
            Files.write(caminho, file.getBytes());

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Comprovante comp = new Comprovante();
            comp.setNomeArquivo(nomeArquivo);
            comp.setUrlArquivo(caminho.toString().replace("\\", "/"));
            comp.setProcessado(false);
            comp.setUsuario(usuario);

            Comprovante salvo = comprovanteRepository.save(comp);

            return new ComprovanteResponseDTO(
                    salvo.getId(),
                    salvo.getNomeArquivo(),
                    salvo.getUrlArquivo(),
                    salvo.getProcessado()
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar arquivo");
        }
    }

    // ==============================
    // ANALISAR (OCR + IA)
    // ==============================
    public String analisar(Long comprovanteId) {

        Comprovante comp = comprovanteRepository.findById(comprovanteId)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        // 🔥 1. OCR
        String texto = ocrService.extrairTexto(comp.getUrlArquivo());

        // 🔥 2. IA
        String resposta = chamarIA(texto);

        // 🔥 3. extrair JSON LIMPO
        String json = extrairJson(resposta);

        // 🔥 4. salvar correto
        comp.setDadosExtraidos(json);
        comp.setProcessado(true);
        comprovanteRepository.save(comp);


        TransacaoIADTO dto = converter(json);


        Usuario usuario = comp.getUsuario();


        Transacao t = new Transacao();

        t.setDescricao(dto.descricao());
        t.setValor(BigDecimal.valueOf(dto.valor()));
        t.setData(LocalDate.parse(dto.data()));


        if (dto.metodoPagamento() != null) {
            try {
                t.setMetodoPagamento(MetodoPagamento.valueOf(dto.metodoPagamento().toUpperCase()));
            } catch (Exception e) {
                t.setMetodoPagamento(MetodoPagamento.PIX);
            }
        } else {
            t.setMetodoPagamento(MetodoPagamento.PIX);
        }


        t.setStatus(StatusPagamento.PAGO);


        t.setUsuario(usuario);


        t.setComprovante(comp);

// categoria (temporário - depois usuário escolhe)
        Categoria categoria = categoriaRepository.findById(1L).orElse(null);
        t.setCategoria(categoria);


        transacaoRepository.save(t);


        return json;
    }

    // ==============================
    // IA (TEXTO → JSON)
    // ==============================
    private String chamarIA(String texto) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:11434/api/generate";

        String prompt = """
Você é um sistema que extrai dados de comprovantes PIX.

⚠️ SUA RESPOSTA DEVE SER APENAS UM JSON VÁLIDO.
⚠️ NÃO escreva explicações.
⚠️ NÃO escreva texto antes ou depois.
⚠️ NÃO use markdown.

RETORNE SOMENTE:

{
  "descricao": "",
  "valor": 0,
  "data": "YYYY-MM-DD",
  "metodoPagamento": "",
  "tipo": "DESPESA"
}

TEXTO:
""" + texto + """

REGRAS:
- descricao = nome do app ou estabelecimento (ex: PicPay)
- valor = número exato (ex: 33.00)
- data = converter corretamente para YYYY-MM-DD
- metodoPagamento = PIX, CARTAO, DINHEIRO (se identificar)
- tipo = DESPESA ou RECEITA
- corrigir erros do OCR
- não inventar dados
- se não souber metodoPagamento, deixar vazio
""";

        Map<String, Object> body = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        Map response = restTemplate.postForObject(url, body, Map.class);

        return (String) response.get("response");
    }

    // ==============================
    // EXTRAIR JSON DA IA
    // ==============================
    private String extrairJson(String resposta) {

        int inicio = resposta.indexOf("{");
        int fim = resposta.lastIndexOf("}");

        if (inicio == -1 || fim == -1) {
            throw new RuntimeException("IA não retornou JSON válido");
        }

        return resposta.substring(inicio, fim + 1);
    }



    private TransacaoIADTO converter(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, TransacaoIADTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON da IA");
        }
    }
}