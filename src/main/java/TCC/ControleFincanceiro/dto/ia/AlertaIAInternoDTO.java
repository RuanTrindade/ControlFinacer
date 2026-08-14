package TCC.ControleFincanceiro.dto.ia;

public record AlertaIAInternoDTO(
        String tipo,
        String categoria,
        String severidade,
        String mensagem
) {
}