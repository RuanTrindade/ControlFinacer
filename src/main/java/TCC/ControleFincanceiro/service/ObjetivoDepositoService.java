package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.objetivo.ObjetivoMovimentacaoDTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.Objetivo;
import TCC.ControleFincanceiro.entity.ObjetivoDeposito;
import TCC.ControleFincanceiro.entity.Transacao;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.ObjetivoDepositoRepository;
import TCC.ControleFincanceiro.repository.ObjetivoRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjetivoDepositoService {

    private final ObjetivoRepository objetivoRepository;
    private final ObjetivoDepositoRepository depositoRepository;
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;


    public ObjetivoMovimentacaoDTO movimentar(Long objetivoId, BigDecimal valor, String tipo) {

        Objetivo objetivo = objetivoRepository.findById(objetivoId)
                .orElseThrow(() -> new RuntimeException("Objetivo não encontrado"));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        // 🚫 BLOQUEIA SE FINALIZADO
        if (objetivo.getFinalizado()) {
            throw new RuntimeException("Objetivo já finalizado");
        }

        BigDecimal saldoAtual = depositoRepository.calcularTotal(objetivoId);
        if (saldoAtual == null) saldoAtual = BigDecimal.ZERO;

        ObjetivoDeposito dep = new ObjetivoDeposito();
        dep.setObjetivo(objetivo);
        dep.setUsuario(objetivo.getUsuario());
        dep.setData(LocalDate.now());


        if (tipo.equalsIgnoreCase("DEPOSITO")) {

            // valida saldo do usuário
            BigDecimal saldoUsuario = transacaoRepository.calcularSaldoUsuario(
                    objetivo.getUsuario().getId()
            );

            if (valor.compareTo(saldoUsuario) > 0) {
                throw new RuntimeException("Saldo insuficiente");
            }

            dep.setValor(valor);

            gerarTransacao(
                    objetivo,
                    valor,
                    "Depósito no objetivo: " + objetivo.getNome(),
                    "Objetivos"
            );
        }


        else if (tipo.equalsIgnoreCase("RETIRADA")) {

            if (valor.compareTo(saldoAtual) > 0) {
                throw new RuntimeException("Saldo insuficiente no objetivo");
            }

            dep.setValor(valor.negate());

            gerarTransacao(
                    objetivo,
                    valor,
                    "Retirada do objetivo: " + objetivo.getNome(),
                    "Resgate de Objetivo"
            );
        }

        else {
            throw new RuntimeException("Tipo inválido. Use DEPOSITO ou RETIRADA");
        }

        depositoRepository.save(dep);

        return new ObjetivoMovimentacaoDTO(
                objetivo.getNome(),
                tipo.toUpperCase(),
                valor,
                dep.getData()
        );
    }


    private void gerarTransacao(Objetivo objetivo, BigDecimal valor, String descricao, String nomeCategoria) {

        Categoria categoria = categoriaRepository
                .findByNomeAndPadraoSistemaTrue(nomeCategoria)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Transacao t = new Transacao();
        t.setDescricao(descricao);
        t.setValor(valor);
        t.setCategoria(categoria);
        t.setUsuario(objetivo.getUsuario());
        t.setStatus(StatusPagamento.PAGO);
        t.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        t.setData(LocalDate.now());

        transacaoRepository.save(t);
    }


    public List<ObjetivoMovimentacaoDTO> listar(Long objetivoId) {

        return depositoRepository.findByObjetivo_IdOrderByDataAsc(objetivoId)
                .stream()
                .map(d -> new ObjetivoMovimentacaoDTO(
                        d.getObjetivo().getNome(),
                        d.getValor().compareTo(BigDecimal.ZERO) > 0 ? "DEPOSITO" : "RETIRADA",
                        d.getValor().abs(),
                        d.getData()
                ))
                .toList();
    }
}