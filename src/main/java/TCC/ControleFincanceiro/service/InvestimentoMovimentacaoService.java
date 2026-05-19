package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.investimento.InvestimentoMovimentacaoDTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.Investimento;
import TCC.ControleFincanceiro.entity.InvestimentoMovimentacao;
import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.InvestimentoMovimentacaoRepository;
import TCC.ControleFincanceiro.repository.InvestimentoRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestimentoMovimentacaoService {

    private final InvestimentoRepository investimentoRepository;
    private final InvestimentoMovimentacaoRepository movimentacaoRepository;
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;



    public InvestimentoMovimentacaoDTO registrarMovimentacao(
            Long investimentoId,
            BigDecimal valor,
            TipoInvestimento tipo
    ) {

        Investimento investimento = investimentoRepository.findById(investimentoId)
                .orElseThrow(() ->
                        new RuntimeException("Investimento não encontrado"));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        InvestimentoMovimentacao movimentacao = new InvestimentoMovimentacao();

        movimentacao.setInvestimento(investimento);
        movimentacao.setData(LocalDate.now());
        movimentacao.setTipo(tipo);



        if (tipo == TipoInvestimento.APORTE) {

            BigDecimal saldoUsuario =
                    transacaoRepository.calcularSaldoUsuario(
                            investimento.getUsuario().getId()
                    );

            if (saldoUsuario == null) {
                saldoUsuario = BigDecimal.ZERO;
            }

            if (valor.compareTo(saldoUsuario) > 0) {
                throw new RuntimeException(
                        "Saldo insuficiente para investimento"
                );
            }

            movimentacao.setValor(valor);

            Transacao transacao = gerarTransacao(
                    investimento,
                    valor,
                    "Aporte em investimento: " + investimento.getNome(),
                    "Investimentos"
            );

            movimentacao.setTransacao(transacao);
        }



        else if (tipo == TipoInvestimento.RESGATE) {

            BigDecimal saldoInvestimento =
                    movimentacaoRepository.calcularSaldo(investimentoId);

            if (saldoInvestimento == null) {
                saldoInvestimento = BigDecimal.ZERO;
            }

            if (valor.compareTo(saldoInvestimento) > 0) {
                throw new RuntimeException(
                        "Saldo insuficiente no investimento"
                );
            }

            movimentacao.setValor(valor.negate());

            Transacao transacao = gerarTransacao(
                    investimento,
                    valor,
                    "Resgate de investimento: " + investimento.getNome(),
                    "Resgate de Investimento"
            );

            movimentacao.setTransacao(transacao);
        }

        else {
            throw new RuntimeException("Tipo inválido");
        }

        movimentacaoRepository.save(movimentacao);

        return new InvestimentoMovimentacaoDTO(
                movimentacao.getId(),
                investimento.getNome(),
                movimentacao.getTipo(),
                movimentacao.getValor().abs(),
                movimentacao.getData()
        );
    }



    private Transacao gerarTransacao(
            Investimento investimento,
            BigDecimal valor,
            String descricao,
            String nomeCategoria
    ) {

        Categoria categoria = categoriaRepository
                .findByNomeAndPadraoSistemaTrue(nomeCategoria)
                .orElseThrow(() ->
                        new RuntimeException("Categoria padrão não encontrada"));

        Transacao transacao = new Transacao();

        transacao.setDescricao(descricao);
        transacao.setValor(valor);
        transacao.setCategoria(categoria);
        transacao.setUsuario(investimento.getUsuario());
        transacao.setStatus(StatusPagamento.PAGO);
        transacao.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        transacao.setData(LocalDate.now());

        return transacaoRepository.save(transacao);
    }



    public List<InvestimentoMovimentacaoDTO> listarMovimentacoes(
            Long investimentoId
    ) {

        return movimentacaoRepository
                .findByInvestimentoIdOrderByDataAsc(investimentoId)
                .stream()
                .map(m -> new InvestimentoMovimentacaoDTO(
                        m.getId(),
                        m.getInvestimento().getNome(),
                        m.getTipo(),
                        m.getValor().abs(),
                        m.getData()
                ))
                .toList();
    }



    public InvestimentoMovimentacaoDTO editarMovimentacao(
            Long movimentacaoId,
            BigDecimal novoValor
    ) {

        InvestimentoMovimentacao movimentacao =
                movimentacaoRepository.findById(movimentacaoId)
                        .orElseThrow(() ->
                                new RuntimeException("Movimentação não encontrada"));

        if (novoValor == null ||
                novoValor.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Valor inválido");
        }

        boolean aporte =
                movimentacao.getTipo() == TipoInvestimento.APORTE;


        movimentacao.setValor(
                aporte
                        ? novoValor
                        : novoValor.negate()
        );

        movimentacaoRepository.save(movimentacao);


        Transacao transacao = movimentacao.getTransacao();

        if (transacao != null) {

            transacao.setValor(novoValor.abs());

            transacaoRepository.save(transacao);
        }

        return new InvestimentoMovimentacaoDTO(
                movimentacao.getId(),
                movimentacao.getInvestimento().getNome(),
                movimentacao.getTipo(),
                movimentacao.getValor().abs(),
                movimentacao.getData()
        );
    }


    public void deletarMovimentacao(Long movimentacaoId) {

        InvestimentoMovimentacao movimentacao =
                movimentacaoRepository.findById(movimentacaoId)
                        .orElseThrow(() ->
                                new RuntimeException("Movimentação não encontrada"));

        Transacao transacao = movimentacao.getTransacao();

        movimentacao.setTransacao(null);
        movimentacaoRepository.save(movimentacao);
        movimentacaoRepository.delete(movimentacao);

        if (transacao != null) {
            transacaoRepository.delete(transacao);
        }
    }
}