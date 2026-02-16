package TCC.ControleFincanceiro.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Entity
public class InvestimentoMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private TipoInvestimento tipo;

    @ManyToOne
    @JoinColumn(name = "investimento_id", nullable = false)
    private Investimento investimento;
}
