package TCC.ControleFincanceiro.service;


import TCC.ControleFincanceiro.dto.TransacaoCriarDTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public Transacao criarTransacao(TransacaoCriarDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor deve ser maior que zero");
        }

        if (!categoria.getPadraoSistema() &&
                (categoria.getUsuario() == null
                || !categoria.getUsuario().getId().equals(usuario.getId()))){

            throw new RuntimeException("Categoria inválida para este usuário");
        }

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);
        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setMetodoPagamento(dto.metodo());
        transacao.setStatus(dto.status());
        transacao.setData(dto.data());

        return transacaoRepository.save(transacao);
    }
}
