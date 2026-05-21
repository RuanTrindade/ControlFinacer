package TCC.ControleFincanceiro.entity;


import TCC.ControleFincanceiro.entity.enumerated.PlanoUsuario;
import TCC.ControleFincanceiro.entity.enumerated.Provider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "usuario")
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

    private LocalDateTime dataCadastro = LocalDateTime.now();


    @Column(unique = true, nullable = true)
    private String googleId;

    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    private Provider provider; // LOCAL ou GOOGLE


    private String tokenRecuperacao;

    private LocalDateTime expiracaoToken;
}
