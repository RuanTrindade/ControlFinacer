package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.categoria.CategoriaAtualizarDTO;
import TCC.ControleFincanceiro.dto.categoria.CategoriaCriarDTO;
import TCC.ControleFincanceiro.dto.categoria.CategoriaResumoDTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public CategoriaResumoDTO criarCategoria(CategoriaCriarDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (categoriaRepository.existsByNomeAndUsuarioId(dto.nome(), dto.usuarioId())) {
            throw new RuntimeException("Categoria já existe para este usuário");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setCor(dto.cor());
        categoria.setIcone(dto.icone());
        categoria.setTipo(dto.tipo());
        categoria.setUsuario(usuario);
        categoria.setPadraoSistema(false);

        Categoria salva = categoriaRepository.save(categoria);

        return toResumoDTO(salva);
    }

    public CategoriaResumoDTO atualizarCategoria(Long categoriaId, Long usuarioId, CategoriaAtualizarDTO dto) {

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (categoria.getPadraoSistema()) {
            throw new RuntimeException("Categorias padrão não podem ser alteradas");
        }

        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado");
        }

        categoria.setNome(dto.nome());
        categoria.setCor(dto.cor());
        categoria.setIcone(dto.icone());
        categoria.setTipo(dto.tipo());

        Categoria salva = categoriaRepository.save(categoria);

        return toResumoDTO(salva);
    }

    public List<CategoriaResumoDTO> listarCategorias(Long usuarioId) {

        return categoriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResumoDTO)
                .toList();
    }

    public void deletarCategoria(Long categoriaId, Long usuarioId) {

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (categoria.getPadraoSistema()) {
            throw new RuntimeException("Categorias padrão não podem ser removidas");
        }

        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado");
        }

        categoriaRepository.delete(categoria);
    }

    private CategoriaResumoDTO toResumoDTO(Categoria categoria) {
        return new CategoriaResumoDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getCor(),
                categoria.getIcone(),
                categoria.getTipo()
        );
    }
}