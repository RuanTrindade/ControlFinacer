package TCC.ControleFincanceiro.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Investimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private BigDecimal valorInicial;
    private BigDecimal valorAtual;
    private BigDecimal taxaRendimento;
    private BigDecimal aporteMensal;
    private Integer prazoMeses;
    private LocalDate dataInicial;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
