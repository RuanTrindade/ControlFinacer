package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.entity.enumerated.Provider;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;



    public Usuario cadastrar(
            String nome,
            String email,
            String senha
    ) {



        if (
                nome == null ||
                        nome.isBlank()
        ) {

            throw new RuntimeException(
                    "Nome obrigatório"
            );
        }



        if (
                email == null ||

                        !email.matches(
                                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                        )
        ) {

            throw new RuntimeException(
                    "Email inválido"
            );
        }



        if (

                senha == null ||

                        senha.length() < 8 ||

                        !senha.matches(".*[A-Z].*") ||

                        !senha.matches(".*[0-9].*") ||

                        !senha.matches(".*[^a-zA-Z0-9].*")
        ) {

            throw new RuntimeException(
                    "Senha fraca"
            );
        }



        if (
                usuarioRepository
                        .findByEmail(email)
                        .isPresent()
        ) {

            throw new RuntimeException(
                    "Email já cadastrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(nome);

        usuario.setEmail(email);

        usuario.setSenha(
                passwordEncoder.encode(senha)
        );

        usuario.setProvider(
                Provider.LOCAL
        );

        return usuarioRepository.save(usuario);
    }



    public void recuperarSenha(
            String email
    ) {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElse(null);



        if (usuario == null) {
            return;
        }



        String token =
                UUID.randomUUID().toString();

        usuario.setTokenRecuperacao(token);

        usuario.setExpiracaoToken(
                LocalDateTime.now()
                        .plusMinutes(30)
        );

        usuarioRepository.save(usuario);


        String link =
                "http://localhost:5173/redefinir-senha?token="
                        + token;



        try {

            MimeMessage mensagem =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mensagem,
                            true,
                            "UTF-8"
                    );

            helper.setTo(
                    usuario.getEmail()
            );

            helper.setSubject(
                    "Recuperação de senha"
            );

            String html = """

        <div style="
            background: #0f172a;
            padding: 50px 20px;
            font-family: Arial, sans-serif;
        ">

            <div style="
                max-width: 600px;
                margin: auto;
                background: #111827;
                border-radius: 24px;
                overflow: hidden;
                border: 1px solid #1f2937;
                box-shadow: 0 0 30px rgba(0,0,0,0.4);
            ">

                <div style="
                    background: linear-gradient(
                        135deg,
                        #16a34a,
                        #22c55e
                    );
                    padding: 40px;
                    text-align: center;
                ">

                    <h1 style="
                        color: white;
                        margin: 0;
                        font-size: 34px;
                        font-weight: bold;
                    ">
                        Controle Financeiro
                    </h1>

                    <p style="
                        color: rgba(255,255,255,0.9);
                        margin-top: 10px;
                        font-size: 15px;
                    ">
                        Segurança da sua conta
                    </p>

                </div>

                <div style="
                    padding: 45px;
                    text-align: center;
                ">

                    <h2 style="
                        color: white;
                        font-size: 28px;
                        margin-bottom: 20px;
                    ">
                        Recuperação de senha
                    </h2>

                    <p style="
                        color: #9ca3af;
                        font-size: 16px;
                        line-height: 1.7;
                        margin-bottom: 35px;
                    ">

                        Recebemos uma solicitação
                        para redefinir sua senha.

                        Clique no botão abaixo
                        para criar uma nova senha
                        de acesso.

                    </p>

                    <a href="%s"
                       style="
                            display: inline-block;
                            background: linear-gradient(
                                135deg,
                                #16a34a,
                                #22c55e
                            );
                            color: white;
                            text-decoration: none;
                            padding: 16px 34px;
                            border-radius: 14px;
                            font-size: 16px;
                            font-weight: bold;
                            box-shadow:
                                0 10px 25px
                                rgba(34,197,94,0.35);
                       ">

                        Redefinir senha

                    </a>

                    <p style="
                        margin-top: 40px;
                        color: #6b7280;
                        font-size: 13px;
                        line-height: 1.6;
                    ">

                        Este link expira em
                        30 minutos.

                        <br><br>

                        Caso você não tenha
                        solicitado a redefinição,
                        ignore este email.

                    </p>

                </div>

                <div style="
                    border-top: 1px solid #1f2937;
                    padding: 20px;
                    text-align: center;
                    color: #6b7280;
                    font-size: 12px;
                    background: #0b1220;
                ">

                    © 2026 Controle Financeiro

                </div>

            </div>

        </div>

        """.formatted(link);

            helper.setText(
                    html,
                    true
            );

            mailSender.send(mensagem);

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao enviar email"
            );
        }
    }



    public void redefinirSenha(
            String token,
            String novaSenha
    ) {

        Usuario usuario =
                usuarioRepository
                        .findByTokenRecuperacao(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Token inválido"
                                )
                        );



        if (
                usuario.getExpiracaoToken()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            throw new RuntimeException(
                    "Token expirado"
            );
        }


        if (

                novaSenha == null ||

                        novaSenha.length() < 8 ||

                        !novaSenha.matches(".*[A-Z].*") ||

                        !novaSenha.matches(".*[0-9].*") ||

                        !novaSenha.matches(".*[^a-zA-Z0-9].*")
        ) {

            throw new RuntimeException(
                    "Senha fraca"
            );
        }


        usuario.setSenha(
                passwordEncoder.encode(
                        novaSenha
                )
        );

        usuario.setTokenRecuperacao(null);

        usuario.setExpiracaoToken(null);

        usuarioRepository.save(usuario);
    }
}