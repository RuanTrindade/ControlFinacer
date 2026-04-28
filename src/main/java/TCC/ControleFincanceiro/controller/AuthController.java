package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.usuario.LoginDTO;
import TCC.ControleFincanceiro.dto.usuario.RegisterDTO;
import TCC.ControleFincanceiro.dto.usuario.UsuarioResponseDTO;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import TCC.ControleFincanceiro.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public UsuarioResponseDTO register(@RequestBody RegisterDTO dto) {

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
    public String login(@RequestBody LoginDTO dto) {

        Usuario user = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.senha(), user.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return "Login realizado com sucesso 🚀";
    }
}