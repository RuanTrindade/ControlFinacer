package TCC.ControleFincanceiro.dto.usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String provider,
        String fotoUrl
) {}