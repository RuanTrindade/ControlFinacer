package TCC.ControleFincanceiro.entity;


import TCC.ControleFincanceiro.entity.enumerated.TipoCategoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String icone;
    private String cor;

    @Enumerated(EnumType.STRING)
    private TipoCategoria tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
