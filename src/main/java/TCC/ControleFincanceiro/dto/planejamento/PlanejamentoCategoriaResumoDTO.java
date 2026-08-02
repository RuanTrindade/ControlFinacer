package TCC.ControleFincanceiro.dto.planejamento;

import java.math.BigDecimal;

public record PlanejamentoCategoriaResumoDTO(

        Long id,
        Long categoriaId,
        String categoria,
        String icone,
        String cor,
        BigDecimal limite,
        BigDecimal pago,
        BigDecimal pendente,
        BigDecimal pagoUltrapassado,
        BigDecimal pendenteUltrapassado,
        BigDecimal restante,
        BigDecimal percentualUtilizado,
        String status

) {}