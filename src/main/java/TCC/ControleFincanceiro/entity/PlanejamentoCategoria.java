package TCC.ControleFincanceiro.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"planejamento_id", "categoria_id"})
        }
)
public class PlanejamentoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal limite;

    @ManyToOne
    @JoinColumn(name = "planejamento_id", nullable = false)
    private PlanejamentoMensal planejamentoMensal;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
