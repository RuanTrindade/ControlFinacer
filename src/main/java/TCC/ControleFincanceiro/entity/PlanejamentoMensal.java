package TCC.ControleFincanceiro.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table( name = "planejamento_mensal",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"referencia", "usuario_id"})
        }
)
public class PlanejamentoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate referencia;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal rendaMensal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualEconomia;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorPlanejado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(
            mappedBy = "planejamentoMensal",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PlanejamentoCategoria> categorias;

}
