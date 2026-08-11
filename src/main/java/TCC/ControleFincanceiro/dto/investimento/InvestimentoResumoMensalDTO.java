package TCC.ControleFincanceiro.dto.investimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoResumoMensalDTO(

        Long investimentoId,
        String nomeInvestimento,
        LocalDate referencia,
        BigDecimal valorNoMes,
        BigDecimal totalAportesMes,
        BigDecimal totalResgatesMes,
        BigDecimal rendimentoMes,
        BigDecimal variacaoDesdeUltimaAtualizacao,
        Boolean atualizadoNoMes,
        LocalDate referenciaUltimaAtualizacaoAnterior

) {
}