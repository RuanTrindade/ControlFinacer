package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.usuario.LoginDTO;
import TCC.ControleFincanceiro.dto.usuario.RecuperarSenhaDTO;
import TCC.ControleFincanceiro.dto.usuario.RedefinirSenhaDTO;
import TCC.ControleFincanceiro.dto.usuario.RegisterDTO;
import TCC.ControleFincanceiro.dto.usuario.UsuarioResponseDTO;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import TCC.ControleFincanceiro.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;



    @PostMapping("/register")
    public UsuarioResponseDTO register(
            @RequestBody RegisterDTO dto
    ) {

        Usuario u = usuarioService.cadastrar(
                dto.nome(),
                dto.email(),
                dto.senha()
        );

        return new UsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getProvider().name(),
                u.getFotoUrl()
        );
    }



    @PostMapping("/login")
    public UsuarioResponseDTO login(@RequestBody LoginDTO dto) {

        Usuario user = usuarioRepository
                .findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.senha(), user.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return new UsuarioResponseDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getProvider().name(),
                user.getFotoUrl()
        );
    }




    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(
            @AuthenticationPrincipal OAuth2User principal
    ) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String email = principal.getAttribute("email");

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getProvider().name(),
                        usuario.getFotoUrl()
                )
        );
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }



    @PostMapping("/recuperar-senha")
    public String recuperarSenha(
            @RequestBody RecuperarSenhaDTO dto
    ) {

        usuarioService.recuperarSenha(
                dto.getEmail()
        );

        return "Email enviado!";
    }



    @PostMapping("/redefinir-senha")
    public String redefinirSenha(
            @RequestBody RedefinirSenhaDTO dto
    ) {

        usuarioService.redefinirSenha(
                dto.getToken(),
                dto.getNovaSenha()
        );

        return "Senha alterada com sucesso!";
    }
}