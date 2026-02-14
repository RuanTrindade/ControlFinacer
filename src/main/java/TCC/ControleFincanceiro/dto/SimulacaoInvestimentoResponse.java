package TCC.ControleFincanceiro.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class SimulacaoInvestimentoResponse {

    private BigDecimal valorFinal;
    private BigDecimal totalInvestido;
    private BigDecimal ganhoObtido;

}