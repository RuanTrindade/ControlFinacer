package TCC.ControleFincanceiro.dto.transacao;

import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


public record TransacaoResumoDTO(
        Long id,
        String descricao,
        String tipo,
        Long categoriaId,
        String categoria,
        String corCategoria,
        String iconeCategoria,
        BigDecimal valor,
        MetodoPagamento metodo,
        StatusPagamento status,
        LocalDate data,
        String urlComprovante
) {
}