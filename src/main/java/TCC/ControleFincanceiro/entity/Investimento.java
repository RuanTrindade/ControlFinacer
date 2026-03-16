package TCC.ControleFincanceiro.entity;


import TCC.ControleFincanceiro.entity.enumerated.TipoAtivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "investimento")
public class Investimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private BigDecimal taxaAtual;

    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "investimento", cascade = CascadeType.ALL)
    private List<InvestimentoMovimentacao> movimentacoes;
}
