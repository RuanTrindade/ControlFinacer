package TCC.ControleFincanceiro.dto.investimento;

import TCC.ControleFincanceiro.entity.enumerated.TipoAtivo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoCardDTO(

        Long id,
        String nome,
        TipoAtivo tipo,
        LocalDate dataCriacao,

        Boolean encerrado,
        Boolean pendenteAtualizacao,

        BigDecimal valorNoMes,
        BigDecimal rendimentoMes

) {
}