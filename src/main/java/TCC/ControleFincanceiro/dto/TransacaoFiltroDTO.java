package TCC.ControleFincanceiro.dto;

import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransacaoFiltroDTO {

    private Long usuarioId;
    private Long categoriaId;

    private StatusPagamento status;
    private MetodoPagamento metodoPagamento;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    private BigDecimal valorMin;
    private BigDecimal valorMax;

    private String descricao;


    //Acho que vou usar no futuro para melhorar o filtro, por enquanto uso uma @Query
}
