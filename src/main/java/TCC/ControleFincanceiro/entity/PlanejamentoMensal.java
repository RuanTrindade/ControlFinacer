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
public class PlanejamentoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate referencia;

    private BigDecimal rendaMensal;

    private BigDecimal percentualEconomia;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "planejamentoMensal", cascade = CascadeType.ALL)
    private List<PlanejamentoCategoria> categorias;

}
