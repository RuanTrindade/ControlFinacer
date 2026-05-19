package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.transacao.*;
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



    public TransacaoResumoDTO criarTransacao(
            TransacaoCriarDTO dto
    ) {

        Usuario usuario =
                usuarioRepository.findById(dto.usuarioId())
                        .orElseThrow(() ->
                                new RuntimeException("Usuário não encontrado"));

        Categoria categoria =
                categoriaRepository.findById(dto.categoriaId())
                        .orElseThrow(() ->
                                new RuntimeException("Categoria não encontrada"));

        if (dto.valor() == null ||
                dto.valor().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Valor deve ser maior que zero"
            );
        }


        if (!categoria.getPadraoSistema() &&
                (
                        categoria.getUsuario() == null
                                || !categoria.getUsuario()
                                .getId()
                                .equals(usuario.getId())
                )) {

            throw new RuntimeException(
                    "Categoria inválida para este usuário"
            );
        }

        Transacao transacao = new Transacao();

        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);
        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setMetodoPagamento(dto.metodo());
        transacao.setStatus(dto.status());
        transacao.setData(dto.data());

        Transacao salva =
                transacaoRepository.save(transacao);

        return toResumoDTO(salva);
    }



    public List<TransacaoResumoDTO> listarPorUsuario(
            Long usuarioId
    ) {

        List<Transacao> transacoes =
                transacaoRepository.findByUsuarioId(usuarioId);

        return transacoes
                .stream()
                .map(this::toResumoDTO)
                .toList();
    }



    public TransacaoResumoDTO buscarPorId(
            Long transacaoId
    ) {

        Transacao transacao =
                transacaoRepository.findById(transacaoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transação não encontrada"
                                ));

        return toResumoDTO(transacao);
    }



    public TransacaoResumoDTO atualizarTransacao(
            Long transacaoId,
            Long usuarioId,
            TransacaoAtualizarDTO dto
    ) {

        Transacao transacao =
                transacaoRepository.findById(transacaoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transação não encontrada"
                                ));



        if (!transacao.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new RuntimeException("Acesso negado");
        }

        Categoria categoria =
                categoriaRepository.findById(dto.categoriaId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria não encontrada"
                                ));



        if (!categoria.getPadraoSistema() &&
                (
                        categoria.getUsuario() == null
                                || !categoria.getUsuario()
                                .getId()
                                .equals(usuarioId)
                )) {

            throw new RuntimeException(
                    "Categoria inválida para este usuário"
            );
        }

        if (dto.valor() == null ||
                dto.valor().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Valor inválido");
        }

        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setMetodoPagamento(dto.metodo());
        transacao.setStatus(dto.status());
        transacao.setData(dto.data());
        transacao.setCategoria(categoria);

        Transacao salva =
                transacaoRepository.save(transacao);

        return toResumoDTO(salva);
    }



    public void deletarTransacao(
            Long transacaoId,
            Long usuarioId
    ) {

        Transacao transacao =
                transacaoRepository.findById(transacaoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transação não encontrada"
                                ));

        if (!transacao.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new RuntimeException("Acesso negado");
        }

        transacaoRepository.delete(transacao);
    }



    public BigDecimal obterSaldoUsuario(
            Long usuarioId
    ) {

        BigDecimal saldo =
                transacaoRepository.calcularSaldoUsuario(usuarioId);

        return saldo != null
                ? saldo
                : BigDecimal.ZERO;
    }



    private TransacaoResumoDTO toResumoDTO(
            Transacao transacao
    ) {

        return new TransacaoResumoDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getCategoria().getTipo().name(),
                transacao.getCategoria().getNome(),
                transacao.getValor(),
                transacao.getMetodoPagamento(),
                transacao.getStatus(),
                transacao.getData()
        );
    }
}