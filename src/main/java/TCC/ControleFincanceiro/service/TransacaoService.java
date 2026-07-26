package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.transacao.TransacaoAtualizarDTO;
import TCC.ControleFincanceiro.dto.transacao.TransacaoCriarDTO;
import TCC.ControleFincanceiro.dto.transacao.TransacaoResumoDTO;

import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.Comprovante;
import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.Usuario;

import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    private final UsuarioRepository usuarioRepository;

    private final CategoriaRepository categoriaRepository;

    private final ComprovanteService comprovanteService;




    public TransacaoResumoDTO criarTransacao(
            TransacaoCriarDTO dto,
            MultipartFile comprovante
    ) {

        Usuario usuario =
                usuarioRepository.findById(dto.usuarioId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuário não encontrado"
                                )
                        );

        Categoria categoria =
                categoriaRepository.findById(dto.categoriaId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria não encontrada"
                                )
                        );

        validarDadosCriacao(
                dto,
                usuario,
                categoria
        );

        Transacao transacao =
                new Transacao();

        transacao.setUsuario(usuario);

        transacao.setCategoria(categoria);

        transacao.setDescricao(
                dto.descricao()
        );

        transacao.setValor(
                dto.valor()
        );

        transacao.setMetodoPagamento(
                dto.metodo()
        );

        transacao.setStatus(
                dto.status()
        );

        transacao.setData(
                dto.data()
        );


        if (
                comprovante != null &&
                        !comprovante.isEmpty()
        ) {

            Comprovante comprovanteSalvo =
                    comprovanteService
                            .salvarComprovante(
                                    comprovante,
                                    usuario
                            );

            transacao.setComprovante(
                    comprovanteSalvo
            );
        }

        Transacao salva =
                transacaoRepository.save(
                        transacao
                );

        return toResumoDTO(
                salva
        );
    }



    public Page<TransacaoResumoDTO> listarPorUsuario(
            Long usuarioId,
            Integer mes,
            Integer ano,
            Pageable pageable
    ) {

        LocalDate dataInicio =
                LocalDate.of(
                        ano,
                        mes,
                        1
                );

        LocalDate dataFim =
                dataInicio.withDayOfMonth(
                        dataInicio.lengthOfMonth()
                );

        Page<Transacao> transacoes =
                transacaoRepository
                        .findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(
                                usuarioId,
                                dataInicio,
                                dataFim,
                                pageable
                        );

        return transacoes.map(this::toResumoDTO);
    }


    public TransacaoResumoDTO buscarPorId(
            Long transacaoId
    ) {

        Transacao transacao =
                transacaoRepository.findById(
                                transacaoId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transação não encontrada"
                                )
                        );

        return toResumoDTO(
                transacao
        );
    }




    public TransacaoResumoDTO atualizarTransacao(
            Long transacaoId,
            Long usuarioId,
            TransacaoAtualizarDTO dto,
            MultipartFile comprovante
    ) {

        Transacao transacao =
                transacaoRepository.findById(
                                transacaoId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transação não encontrada"
                                )
                        );

        if (
                !transacao
                        .getUsuario()
                        .getId()
                        .equals(usuarioId)
        ) {

            throw new RuntimeException(
                    "Acesso negado"
            );
        }

        Categoria categoria =
                categoriaRepository.findById(
                                dto.categoriaId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria não encontrada"
                                )
                        );

        validarCategoriaDoUsuario(
                categoria,
                usuarioId
        );

        if (
                dto.valor() == null ||
                        dto.valor()
                                .compareTo(
                                        BigDecimal.ZERO
                                ) <= 0
        ) {

            throw new RuntimeException(
                    "Valor inválido"
            );
        }

        if (
                dto.descricao() == null ||
                        dto.descricao().isBlank()
        ) {

            throw new RuntimeException(
                    "Descrição é obrigatória"
            );
        }

        if (dto.metodo() == null) {

            throw new RuntimeException(
                    "Método de pagamento é obrigatório"
            );
        }

        if (dto.status() == null) {

            throw new RuntimeException(
                    "Status é obrigatório"
            );
        }

        if (dto.data() == null) {

            throw new RuntimeException(
                    "Data é obrigatória"
            );
        }

        transacao.setDescricao(
                dto.descricao()
        );

        transacao.setValor(
                dto.valor()
        );

        transacao.setMetodoPagamento(
                dto.metodo()
        );

        transacao.setStatus(
                dto.status()
        );

        transacao.setData(
                dto.data()
        );

        transacao.setCategoria(
                categoria
        );


        boolean deveRemoverComprovante =
                Boolean.TRUE.equals(
                        dto.removerComprovante()
                );

        if (deveRemoverComprovante) {

            transacao.setComprovante(
                    null
            );
        }

        if (
                comprovante != null &&
                        !comprovante.isEmpty()
        ) {

            Comprovante comprovanteSalvo =
                    comprovanteService
                            .salvarComprovante(
                                    comprovante,
                                    transacao.getUsuario()
                            );

            transacao.setComprovante(
                    comprovanteSalvo
            );
        }

        Transacao salva =
                transacaoRepository.save(
                        transacao
                );

        return toResumoDTO(
                salva
        );
    }



    public void deletarTransacao(
            Long transacaoId,
            Long usuarioId
    ) {

        Transacao transacao =
                transacaoRepository.findById(
                                transacaoId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transação não encontrada"
                                )
                        );

        if (
                !transacao
                        .getUsuario()
                        .getId()
                        .equals(usuarioId)
        ) {

            throw new RuntimeException(
                    "Acesso negado"
            );
        }

        transacaoRepository.delete(
                transacao
        );
    }


    public Page<TransacaoResumoDTO> filtrarTransacoes(
            Long usuarioId,
            Long categoriaId,
            StatusPagamento status,
            MetodoPagamento metodoPagamento,
            LocalDate dataInicio,
            LocalDate dataFim,
            BigDecimal valorMin,
            BigDecimal valorMax,
            String descricao,
            Pageable pageable
    ) {

        if (usuarioId == null) {
            throw new RuntimeException(
                    "Usuário é obrigatório"
            );
        }

        if (
                valorMin != null &&
                        valorMin.compareTo(BigDecimal.ZERO) < 0
        ) {
            throw new RuntimeException(
                    "O valor mínimo não pode ser negativo"
            );
        }

        if (
                valorMax != null &&
                        valorMax.compareTo(BigDecimal.ZERO) < 0
        ) {
            throw new RuntimeException(
                    "O valor máximo não pode ser negativo"
            );
        }

        if (
                valorMin != null &&
                        valorMax != null &&
                        valorMin.compareTo(valorMax) > 0
        ) {
            throw new RuntimeException(
                    "O valor mínimo não pode ser maior que o valor máximo"
            );
        }

        if (
                dataInicio != null &&
                        dataFim != null &&
                        dataInicio.isAfter(dataFim)
        ) {
            throw new RuntimeException(
                    "A data inicial não pode ser posterior à data final"
            );
        }

        String descricaoTratada =
                descricao == null ||
                        descricao.isBlank()
                        ? null
                        : descricao.trim();

        Page<Transacao> transacoes =
                transacaoRepository.filtrar(
                        usuarioId,
                        categoriaId,
                        status,
                        metodoPagamento,
                        dataInicio,
                        dataFim,
                        valorMin,
                        valorMax,
                        descricaoTratada,
                        pageable
                );

        return transacoes.map(this::toResumoDTO);
    }

    public BigDecimal obterSaldoUsuario(
            Long usuarioId
    ) {

        BigDecimal saldo =
                transacaoRepository
                        .calcularSaldoUsuario(
                                usuarioId
                        );

        return saldo != null
                ? saldo
                : BigDecimal.ZERO;
    }



    private void validarDadosCriacao(
            TransacaoCriarDTO dto,
            Usuario usuario,
            Categoria categoria
    ) {

        if (
                dto.descricao() == null ||
                        dto.descricao().isBlank()
        ) {

            throw new RuntimeException(
                    "Descrição é obrigatória"
            );
        }

        if (
                dto.valor() == null ||
                        dto.valor()
                                .compareTo(
                                        BigDecimal.ZERO
                                ) <= 0
        ) {

            throw new RuntimeException(
                    "Valor deve ser maior que zero"
            );
        }

        if (dto.metodo() == null) {

            throw new RuntimeException(
                    "Método de pagamento é obrigatório"
            );
        }

        if (dto.status() == null) {

            throw new RuntimeException(
                    "Status é obrigatório"
            );
        }

        if (dto.data() == null) {

            throw new RuntimeException(
                    "Data é obrigatória"
            );
        }

        validarCategoriaDoUsuario(
                categoria,
                usuario.getId()
        );
    }




    private void validarCategoriaDoUsuario(
            Categoria categoria,
            Long usuarioId
    ) {

        boolean categoriaDoSistema =
                Boolean.TRUE.equals(
                        categoria.getPadraoSistema()
                );

        boolean categoriaDoUsuario =
                categoria.getUsuario() != null &&
                        categoria
                                .getUsuario()
                                .getId()
                                .equals(usuarioId);

        if (
                !categoriaDoSistema &&
                        !categoriaDoUsuario
        ) {

            throw new RuntimeException(
                    "Categoria inválida para este usuário"
            );
        }
    }




    private TransacaoResumoDTO toResumoDTO(
            Transacao transacao
    ) {

        String urlComprovante =
                transacao.getComprovante() != null
                        ? transacao
                          .getComprovante()
                          .getUrlArquivo()
                        : null;

        return new TransacaoResumoDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getCategoria().getTipo().name(),
                transacao.getCategoria().getId(),
                transacao.getCategoria().getNome(),
                transacao.getCategoria().getCor(),
                transacao.getCategoria().getIcone(),
                transacao.getValor(),
                transacao.getMetodoPagamento(),
                transacao.getStatus(),
                transacao.getData(),
                transacao.getComprovante() != null
                        ? transacao.getComprovante().getUrlArquivo()
                        : null
        );
    }
}