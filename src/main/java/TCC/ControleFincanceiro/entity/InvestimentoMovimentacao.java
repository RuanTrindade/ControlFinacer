package TCC.ControleFincanceiro.entity;

import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "investimento_movimentacao")
public class InvestimentoMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal valor;


    @Column(nullable = false)
    private LocalDateTime data;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInvestimento tipo;


    @ManyToOne
    @JoinColumn(
            name = "investimento_id",
            nullable = false
    )
    private Investimento investimento;


    @OneToOne
    @JoinColumn(name = "transacao_id")
    private Transacao transacao;
}