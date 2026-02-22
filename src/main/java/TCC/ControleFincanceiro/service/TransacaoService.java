package TCC.ControleFincanceiro.service;


import TCC.ControleFincanceiro.dto.TransacaoAtualizarDTO;
import TCC.ControleFincanceiro.dto.TransacaoCriarDTO;
import TCC.ControleFincanceiro.dto.TransacaoResumoDTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public TransacaoResumoDTO criarTransacao(TransacaoCriarDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (dto.valor() == null || dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
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

        Transacao salvar = transacaoRepository.save(transacao);

        return new TransacaoResumoDTO(
                salvar.getDescricao(),
                salvar.getCategoria().getTipo().name(),
                salvar.getCategoria().getNome(),
                salvar.getValor(),
                salvar.getMetodoPagamento(),
                salvar.getStatus(),
                salvar.getData()
        );
    }



    public TransacaoResumoDTO atualizarTransacao(Long trasacaoId, Long usuarioId, TransacaoAtualizarDTO dto) {

        Transacao transacao = transacaoRepository.findById(trasacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado");
        }

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (!categoria.getPadraoSistema() &&
                (categoria.getUsuario() == null
                || !categoria.getUsuario().getId().equals(usuarioId))){
            throw new RuntimeException("Categoria inválida para este usuario");
        }

        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setMetodoPagamento(dto.metodo());
        transacao.setStatus(dto.status());
        transacao.setData(dto.data());
        transacao.setCategoria(categoria);

        Transacao salva = transacaoRepository.save(transacao);

        return new TransacaoResumoDTO(
                salva.getDescricao(),
                salva.getCategoria().getTipo().name(),
                salva.getCategoria().getNome(),
                salva.getValor(),
                salva.getMetodoPagamento(),
                salva.getStatus(),
                salva.getData()
        );
    }




    public List<TransacaoResumoDTO> listarPorUsuario(Long usuarioId) {

        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuarioId);

        return transacoes.stream()
                .map(t -> new TransacaoResumoDTO(
                        t.getDescricao(),
                        t.getCategoria().getTipo().name(),
                        t.getCategoria().getNome(),
                        t.getValor(),
                        t.getMetodoPagamento(),
                        t.getStatus(),
                        t.getData()
                ))
                .toList();
    }
}
