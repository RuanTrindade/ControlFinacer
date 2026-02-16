package TCC.ControleFincanceiro.entity;

import TCC.ControleFincanceiro.entity.enumerated.transacao.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.transacao.StatusPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;


    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String urlAnexo;


    //getter e setter
}

