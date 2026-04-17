package TCC.ControleFincanceiro.dto.comprovante;

public record ComprovanteResponseDTO(
        Long id,
        String nomeArquivo,
        String urlArquivo,
        Boolean processado
) {}