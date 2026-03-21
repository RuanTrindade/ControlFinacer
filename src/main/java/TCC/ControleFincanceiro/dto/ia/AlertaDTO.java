package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record AlertaDTO(
        String categoria,
        BigDecimal gasto,
        BigDecimal limite,
        BigDecimal excesso,
        String mensagem
) {}