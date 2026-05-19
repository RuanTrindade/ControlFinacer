package TCC.ControleFincanceiro.dto.transacao;

import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResumoDTO(
        Long id, String descricao,
        String tipo,
        String categoria,
        BigDecimal valor,
        MetodoPagamento metodo,
        StatusPagamento status,
        LocalDate data
) {}
