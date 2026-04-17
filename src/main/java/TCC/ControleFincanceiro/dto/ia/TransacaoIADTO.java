package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoIADTO(
        String descricao,
        Double valor,
        String data,
        String metodoPagamento,
        String tipo
) {}