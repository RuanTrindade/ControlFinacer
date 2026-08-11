package TCC.ControleFincanceiro.dto.investimento;

import TCC.ControleFincanceiro.entity.enumerated.TipoAtivo;

import java.time.LocalDate;

public record InvestimentoResumoDTO(

        Long id,
        String nome,
        TipoAtivo tipo,
        LocalDate dataCriacao,
        Boolean encerrado,
        Boolean pendenteAtualizacao

) {
}