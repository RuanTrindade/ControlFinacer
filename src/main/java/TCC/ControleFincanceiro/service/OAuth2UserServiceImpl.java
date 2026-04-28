package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.entity.enumerated.Provider;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {

        OAuth2User user = super.loadUser(request);

        String email = user.getAttribute("email");
        String nome = user.getAttribute("name");
        String googleId = user.getAttribute("sub");
        String foto = user.getAttribute("picture");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .map(u -> {
                    // 🔥 atualiza dados se já existir
                    u.setNome(nome);
                    u.setGoogleId(googleId);
                    u.setFotoUrl(foto);
                    u.setProvider(Provider.GOOGLE);
                    return usuarioRepository.save(u);
                })
                .orElseGet(() -> {
                    // 🔥 cria novo se não existir
                    Usuario novo = new Usuario();
                    novo.setEmail(email);
                    novo.setNome(nome);
                    novo.setGoogleId(googleId);
                    novo.setFotoUrl(foto);
                    novo.setProvider(Provider.GOOGLE);
                    return usuarioRepository.save(novo);
                });

        return user;
    }
}