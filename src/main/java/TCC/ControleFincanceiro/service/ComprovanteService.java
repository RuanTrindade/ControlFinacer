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




    private String chamarIA(
            String texto
    ) {

        RestTemplate restTemplate =
                new RestTemplate();


        String url =
                "http://localhost:11434/api/generate";


        String prompt =
                """
                Você extrai informações de comprovantes financeiros.
    
                Responda SOMENTE JSON válido.
    
                Não use markdown.
                Não escreva explicações.
                Não invente dados.
    
                Estrutura obrigatória:
    
                {
                  "descricao": "",
                  "valor": 0,
                  "data": "",
                  "metodoPagamento": "",
                  "tipo": ""
                }
    
                REGRAS:
    
                descricao:
                nome do estabelecimento, pessoa ou descrição
                identificável no comprovante.
    
                valor:
                valor principal da transação.
    
                data:
                formato YYYY-MM-DD.
                Se não identificar com segurança, deixe "".
    
                metodoPagamento:
                use somente:
                PIX
                BOLETO
                CARTAO_CREDITO
                DEBITO
                DINHEIRO
                TRANSFERENCIA
    
                Se não souber, deixe "".
    
                tipo:
                use somente:
                RECEITA
                DESPESA
    
                Se não for possível determinar com segurança,
                deixe "".
    
                Corrija apenas erros claros causados pelo OCR.
    
                TEXTO EXTRAÍDO:
    
                """
                        +
                        texto;


        Map<String, Object> body =
                Map.of(
                        "model",
                        "llama3",
                        "prompt",
                        prompt,
                        "stream",
                        false,
                        "format",
                        "json",
                        "options",
                        Map.of(
                                "temperature",
                                0.1
                        )
                );


        Map<?, ?> response =
                restTemplate.postForObject(
                        url,
                        body,
                        Map.class
                );


        if (
                response == null ||
                        response.get("response") == null
        ) {

            throw new RuntimeException(
                    "A IA não retornou resposta."
            );

        }


        return response
                .get("response")
                .toString();
    }

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

    public Comprovante salvarComprovante(
            MultipartFile file,
            Usuario usuario
    ) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {

            String nomeOriginal =
                    file.getOriginalFilename();

            String nomeArquivo =
                    System.currentTimeMillis()
                            + "_"
                            + nomeOriginal;

            Path caminho =
                    Paths.get(
                            "uploads",
                            nomeArquivo
                    );

            Files.createDirectories(
                    caminho.getParent()
            );

            Files.write(
                    caminho,
                    file.getBytes()
            );

            Comprovante comprovante =
                    new Comprovante();

            comprovante.setNomeArquivo(
                    nomeArquivo
            );

            comprovante.setUrlArquivo(
                    caminho
                            .toString()
                            .replace("\\", "/")
            );

            comprovante.setProcessado(false);

            comprovante.setUsuario(usuario);

            return comprovanteRepository.save(
                    comprovante
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao salvar comprovante",
                    e
            );
        }
    }


    public TransacaoIADTO analisarArquivo(
            MultipartFile file
    ) {

        Path caminhoTemporario = null;

        try {

            if (
                    file == null ||
                            file.isEmpty()
            ) {

                throw new RuntimeException(
                        "Nenhum comprovante enviado."
                );
            }


            String nomeOriginal =
                    file.getOriginalFilename();


            String extensao =
                    "";

            if (
                    nomeOriginal != null &&
                            nomeOriginal.contains(".")
            ) {

                extensao =
                        nomeOriginal.substring(
                                nomeOriginal.lastIndexOf(".")
                        );
            }


            caminhoTemporario =
                    Files.createTempFile(
                            "comprovante_",
                            extensao
                    );


            Files.write(
                    caminhoTemporario,
                    file.getBytes()
            );


            String texto =
                    ocrService.extrairTexto(
                            caminhoTemporario.toString()
                    );


            String respostaIA =
                    chamarIA(
                            texto
                    );


            String json =
                    extrairJson(
                            respostaIA
                    );


            return converter(
                    json
            );

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao analisar comprovante: "
                            +
                            e.getMessage()
            );

        }
        finally {

            if (
                    caminhoTemporario != null
            ) {

                try {

                    Files.deleteIfExists(
                            caminhoTemporario
                    );

                }
                catch (Exception ignored) {
                }

            }

        }
    }
}


