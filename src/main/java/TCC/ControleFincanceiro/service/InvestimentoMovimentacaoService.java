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
                .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        InvestimentoMovimentacao movimentacao = new InvestimentoMovimentacao();
        movimentacao.setInvestimento(investimento);
        movimentacao.setValor(valor);
        movimentacao.setData(LocalDate.now());
        movimentacao.setTipo(tipo);

        movimentacaoRepository.save(movimentacao);

        if (tipo == TipoInvestimento.APORTE) {
            gerarTransacao(investimento, valor, "Aporte em investimento: " + investimento.getNome(), "Investimento");
        }

        if (tipo == TipoInvestimento.RESGATE) {
            gerarTransacao(investimento, valor, "Resgate de investimento: " + investimento.getNome(), "Resgate de Investimento");
        }

        return new InvestimentoMovimentacaoDTO(
                investimento.getNome(),
                movimentacao.getTipo(),
                movimentacao.getValor(),
                movimentacao.getData()
        );
    }

    private void gerarTransacao(Investimento investimento, BigDecimal valor, String descricao, String nomeCategoria) {

        Categoria categoria = categoriaRepository
                .findByNomeAndPadraoSistemaTrue(nomeCategoria)
                .orElseThrow(() -> new RuntimeException("Categoria padrão não encontrada"));

        Transacao transacao = new Transacao();
        transacao.setDescricao(descricao);
        transacao.setValor(valor);
        transacao.setCategoria(categoria);
        transacao.setUsuario(investimento.getUsuario());
        transacao.setStatus(StatusPagamento.PAGO);
        transacao.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        transacao.setData(LocalDate.now());

        transacaoRepository.save(transacao);
    }
}