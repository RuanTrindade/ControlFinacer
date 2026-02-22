package TCC.ControleFincanceiro.dto;

import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoAtualizarDTO (
    Long categoriaId,
    String descricao,
    BigDecimal valor,
    MetodoPagamento metodo,
    StatusPagamento status,
    LocalDate data

){}
