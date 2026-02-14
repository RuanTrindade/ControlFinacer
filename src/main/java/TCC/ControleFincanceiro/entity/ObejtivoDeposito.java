package TCC.ControleFincanceiro.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class ObejtivoDeposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor;

    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "objetivo_id", nullable = false)
    private Objetivo objetivo;
}
