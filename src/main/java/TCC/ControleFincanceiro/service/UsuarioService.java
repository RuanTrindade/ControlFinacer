package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.entity.enumerated.Provider;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario cadastrar(String nome, String email, String senha) {

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(passwordEncoder.encode(senha)); // 🔐 criptografa
        u.setProvider(Provider.LOCAL);

        return usuarioRepository.save(u);
    }
}