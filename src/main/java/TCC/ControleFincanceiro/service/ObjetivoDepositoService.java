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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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



    public ObjetivoMovimentacaoDTO movimentar(
            Long objetivoId,
            BigDecimal valor,
            String tipo
    ) {

        Objetivo objetivo = objetivoRepository.findById(objetivoId)
                .orElseThrow(() ->
                        new RuntimeException("Objetivo não encontrado"));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        if (objetivo.getFinalizado()) {
            throw new RuntimeException("Objetivo já finalizado");
        }

        BigDecimal saldoAtual =
                depositoRepository.calcularTotal(objetivoId);

        if (saldoAtual == null) {
            saldoAtual = BigDecimal.ZERO;
        }

        ObjetivoDeposito dep = new ObjetivoDeposito();

        dep.setObjetivo(objetivo);
        dep.setUsuario(objetivo.getUsuario());
        dep.setData(LocalDate.now());



        if (tipo.equalsIgnoreCase("DEPOSITO")) {

            BigDecimal saldoUsuario =
                    transacaoRepository.calcularSaldoUsuario(
                            objetivo.getUsuario().getId(),
                            StatusPagamento.PAGO
                    );
            if (saldoUsuario == null) {
                saldoUsuario = BigDecimal.ZERO;
            }

            if (valor.compareTo(saldoUsuario) > 0) {
                throw new RuntimeException("Saldo insuficiente");
            }

            dep.setValor(valor);

            Transacao transacao = gerarTransacao(
                    objetivo,
                    valor,
                    "Depósito no objetivo: " + objetivo.getNome(),
                    "Objetivos"
            );

            dep.setTransacao(transacao);
        }



        else if (tipo.equalsIgnoreCase("RETIRADA")) {

            if (valor.compareTo(saldoAtual) > 0) {
                throw new RuntimeException(
                        "Saldo insuficiente no objetivo"
                );
            }

            dep.setValor(valor.negate());

            Transacao transacao = gerarTransacao(
                    objetivo,
                    valor,
                    "Retirada do objetivo: " + objetivo.getNome(),
                    "Resgate de Objetivo"
            );

            dep.setTransacao(transacao);
        }

        else {
            throw new RuntimeException(
                    "Tipo inválido. Use DEPOSITO ou RETIRADA"
            );
        }

        depositoRepository.save(
                dep
        );


        /*
         * Somente um depósito pode levar
         * o objetivo à conclusão.
         */
        if (
                tipo.equalsIgnoreCase(
                        "DEPOSITO"
                )
        ) {

            verificarConclusao(
                    objetivo
            );
        }


        return new ObjetivoMovimentacaoDTO(
                dep.getId(),
                objetivo.getNome(),
                tipo.toUpperCase(),
                valor,
                dep.getData()
        );
    }



    private Transacao gerarTransacao(
            Objetivo objetivo,
            BigDecimal valor,
            String descricao,
            String nomeCategoria
    ) {

        Categoria categoria = categoriaRepository
                .findByNomeAndPadraoSistemaTrue(nomeCategoria)
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada"));

        Transacao t = new Transacao();

        t.setDescricao(descricao);
        t.setValor(valor);
        t.setCategoria(categoria);
        t.setUsuario(objetivo.getUsuario());
        t.setStatus(StatusPagamento.PAGO);
        t.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        t.setData(LocalDate.now());

        return transacaoRepository.save(t);
    }



    public Page<ObjetivoMovimentacaoDTO> listar(
            Long objetivoId,
            Pageable pageable
    ) {

        return depositoRepository
                .findByObjetivoIdOrderByDataDesc(
                        objetivoId,
                        pageable
                )
                .map(
                        d ->
                                new ObjetivoMovimentacaoDTO(
                                        d.getId(),
                                        d.getObjetivo().getNome(),
                                        d.getValor()
                                                .compareTo(
                                                        BigDecimal.ZERO
                                                ) > 0
                                                ? "DEPOSITO"
                                                : "RETIRADA",
                                        d.getValor().abs(),
                                        d.getData()
                                )
                );
    }



    public ObjetivoMovimentacaoDTO editar(
            Long depositoId,
            BigDecimal novoValor
    ) {

        ObjetivoDeposito dep = depositoRepository.findById(depositoId)
                .orElseThrow(() ->
                        new RuntimeException("Movimentação não encontrada"));

        if (
                Boolean.TRUE.equals(
                        dep.getObjetivo().getFinalizado()
                )
        ) {

            throw new RuntimeException(
                    "Não é possível editar movimentações de um objetivo concluído"
            );
        }

        if (novoValor == null ||
                novoValor.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("Valor inválido");
        }

        boolean deposito =
                dep.getValor().compareTo(BigDecimal.ZERO) > 0;

        // 🔥 atualiza depósito
        dep.setValor(
                deposito
                        ? novoValor
                        : novoValor.negate()
        );

        depositoRepository.save(dep);

        Transacao t = dep.getTransacao();

        if (t != null) {

            t.setValor(novoValor.abs());

            transacaoRepository.save(t);
        }

        /*
         * Uma edição também pode fazer
         * o objetivo atingir a meta.
         */
        verificarConclusao(
                dep.getObjetivo()
        );
        return new ObjetivoMovimentacaoDTO(
                dep.getId(),
                dep.getObjetivo().getNome(),
                deposito ? "DEPOSITO" : "RETIRADA",
                novoValor.abs(),
                dep.getData()
        );
    }



    public void deletar(Long depositoId) {

        ObjetivoDeposito dep = depositoRepository.findById(depositoId)
                .orElseThrow(() ->
                        new RuntimeException("Movimentação não encontrada"));

        if (
                Boolean.TRUE.equals(
                        dep.getObjetivo().getFinalizado()
                )
        ) {

            throw new RuntimeException(
                    "Não é possível excluir movimentações de um objetivo concluído"
            );
        }



        Transacao transacao = dep.getTransacao();

        dep.setTransacao(null);
        depositoRepository.save(dep);
        depositoRepository.delete(dep);

        if (transacao != null) {
            transacaoRepository.delete(transacao);
        }
    }

    private void verificarConclusao(
            Objetivo objetivo
    ) {

        /*
         * Se já terminou, não fazemos nada.
         *
         * Isso garante que a transação de
         * conclusão nunca será criada duas vezes.
         */
        if (
                Boolean.TRUE.equals(
                        objetivo.getFinalizado()
                )
        ) {
            return;
        }


        BigDecimal total =
                depositoRepository.calcularTotal(
                        objetivo.getId()
                );


        if (total == null) {
            total = BigDecimal.ZERO;
        }


        /*
         * Ainda não atingiu a meta.
         */
        if (
                total.compareTo(
                        objetivo.getValorObjetivo()
                ) < 0
        ) {
            return;
        }


        /*
         * Busca a categoria técnica de receita.
         */
        Categoria categoria =
                categoriaRepository
                        .findByNomeAndPadraoSistemaTrue(
                                "Resgate de Objetivo"
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria de conclusão de objetivo não encontrada"
                                )
                        );


        /*
         * Cria a receita que devolve o dinheiro
         * acumulado para a conta do usuário.
         */
        Transacao transacao =
                new Transacao();


        transacao.setDescricao(
                "Objetivo concluído: "
                        + objetivo.getNome()
        );


        transacao.setValor(
                total
        );


        transacao.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );


        transacao.setStatus(
                StatusPagamento.PAGO
        );


        transacao.setData(
                LocalDate.now()
        );


        transacao.setCategoria(
                categoria
        );


        transacao.setUsuario(
                objetivo.getUsuario()
        );


        /*
         * Essa transação não poderá ser
         * alterada manualmente.
         */
        transacao.setBloqueadaSistema(
                true
        );


        transacaoRepository.save(
                transacao
        );


        /*
         * Só depois da transação ser criada
         * marcamos o objetivo como concluído.
         */
        objetivo.setFinalizado(
                true
        );


        objetivoRepository.save(
                objetivo
        );
    }
}