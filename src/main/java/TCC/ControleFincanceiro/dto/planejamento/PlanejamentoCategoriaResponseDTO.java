package TCC.ControleFincanceiro.dto.planejamento;


import java.math.BigDecimal;

public record PlanejamentoCategoriaResponseDTO(
        Long id,
        String categoria,
        BigDecimal limite
) {}