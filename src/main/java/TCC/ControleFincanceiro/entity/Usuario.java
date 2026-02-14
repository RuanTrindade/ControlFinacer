package TCC.ControleFincanceiro.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;
    private String senha;

    @Enumerated(EnumType.STRING)
    private PlanoUsuario plano;

    @Enumerated(EnumType.STRING)
    private StatusUsuario status;

    private LocalDateTime dataCadastro = LocalDateTime.now();


}
