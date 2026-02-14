package TCC.ControleFincanceiro.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



@Getter
@Setter
public class SimulacaoInvestimentoRequest {

    private BigDecimal valorInicial;
    private BigDecimal aporteMensal;
    private BigDecimal taxaRendimento;
    private Integer prazoMeses;

}