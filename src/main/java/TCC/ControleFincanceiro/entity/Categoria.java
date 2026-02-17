package TCC.ControleFincanceiro.entity;


import TCC.ControleFincanceiro.entity.enumerated.TipoTransacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String icone;
    private String cor;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private Boolean padraoSistema = false;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
