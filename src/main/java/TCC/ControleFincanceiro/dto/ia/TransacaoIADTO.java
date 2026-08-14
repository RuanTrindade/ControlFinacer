package TCC.ControleFincanceiro.dto.ia;

import java.math.BigDecimal;

public record TransacaoIADTO(

        String descricao,
        BigDecimal valor,
        String data,
        String metodoPagamento,
        String tipo

) {
}