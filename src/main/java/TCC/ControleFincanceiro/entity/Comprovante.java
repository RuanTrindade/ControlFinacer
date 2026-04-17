package TCC.ControleFincanceiro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Comprovante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;

    private String urlArquivo;

    private Boolean processado; // IA analisou?

    @Column(columnDefinition = "TEXT")
    private String dadosExtraidos;

    @ManyToOne
    private Usuario usuario;
}