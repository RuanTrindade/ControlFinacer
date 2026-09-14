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

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;


@Service
@RequiredArgsConstructor
public class InvestimentoMovimentacaoService {


    private final InvestimentoRepository investimentoRepository;

    private final InvestimentoMovimentacaoRepository movimentacaoRepository;

    private final TransacaoRepository transacaoRepository;

    private final CategoriaRepository categoriaRepository;

    private final InvestimentoService investimentoService;


    /* ===================================================
       REGISTRAR MOVIMENTAÇÃO
    =================================================== */

    @Transactional
    public InvestimentoMovimentacaoDTO registrarMovimentacao(

            Long investimentoId,

            BigDecimal valor,

            TipoInvestimento tipo,

            LocalDate referencia

    ) {

        Investimento investimento =
                buscarInvestimento(
                        investimentoId
                );


        if (
                Boolean.TRUE.equals(
                        investimento.getEncerrado()
                )
        ) {

            throw new RuntimeException(
                    "Investimento encerrado não aceita novas movimentações"
            );
        }


        validarValor(
                valor
        );


        if (tipo == null) {

            throw new RuntimeException(
                    "Tipo da movimentação é obrigatório"
            );
        }


        InvestimentoMovimentacao movimentacao =
                new InvestimentoMovimentacao();


        movimentacao.setInvestimento(
                investimento
        );


        movimentacao.setTipo(
                tipo
        );


        movimentacao.setValor(
                valor
        );


        /* ===================================================
           APORTE
        =================================================== */

        if (
                tipo ==
                        TipoInvestimento.APORTE
        ) {

            /*
             * Aporte utiliza a data atual.
             */
            movimentacao.setData(
                    LocalDateTime.now()
            );


            registrarAporte(
                    investimento,
                    movimentacao,
                    valor
            );

        }


        /* ===================================================
           RESGATE
        =================================================== */

        else if (
                tipo ==
                        TipoInvestimento.RESGATE
        ) {

            /*
             * Resgate utiliza a data atual.
             */
            movimentacao.setData(
                    LocalDateTime.now()
            );


            registrarResgate(
                    investimento,
                    movimentacao,
                    valor
            );

        }


        /* ===================================================
           RENDIMENTO
        =================================================== */

        else if (
                tipo ==
                        TipoInvestimento.RENDIMENTO
        ) {

            registrarRendimento(
                    investimento,
                    movimentacao,
                    referencia
            );

        } else {

            throw new RuntimeException(
                    "Tipo de movimentação inválido"
            );

        }


        InvestimentoMovimentacao salva =
                movimentacaoRepository
                        .save(
                                movimentacao
                        );


        return toDTO(
                salva
        );
    }


    /* ===================================================
       APORTE
    =================================================== */

    private void registrarAporte(

            Investimento investimento,

            InvestimentoMovimentacao movimentacao,

            BigDecimal valor

    ) {

        BigDecimal saldoUsuario =
                transacaoRepository
                        .calcularSaldoUsuario(

                                investimento
                                        .getUsuario()
                                        .getId(),

                                StatusPagamento.PAGO
                        );


        if (saldoUsuario == null) {

            saldoUsuario =
                    BigDecimal.ZERO;
        }


        if (
                valor.compareTo(
                        saldoUsuario
                ) > 0
        ) {

            throw new RuntimeException(
                    "Saldo insuficiente para realizar o aporte"
            );
        }


        /*
         * Aporte retira dinheiro
         * do saldo geral do usuário.
         */
        Transacao transacao =
                gerarTransacao(

                        investimento,

                        valor,

                        "Aporte em investimento: "
                                + investimento.getNome(),

                        "Investimentos"
                );


        movimentacao.setTransacao(
                transacao
        );
    }


    /* ===================================================
       RESGATE
    =================================================== */

    private void registrarResgate(

            Investimento investimento,

            InvestimentoMovimentacao movimentacao,

            BigDecimal valor

    ) {

        BigDecimal saldoInvestimento =
                investimentoService
                        .calcularSaldoAtual(
                                investimento.getId()
                        );


        if (saldoInvestimento == null) {

            saldoInvestimento =
                    BigDecimal.ZERO;
        }


        if (
                saldoInvestimento.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {

            throw new RuntimeException(
                    "Este investimento não possui saldo disponível para resgate"
            );
        }


        if (
                valor.compareTo(
                        saldoInvestimento
                ) > 0
        ) {

            throw new RuntimeException(
                    "Saldo insuficiente no investimento"
            );
        }


        /*
         * Resgate devolve dinheiro
         * para o saldo geral do usuário.
         */
        Transacao transacao =
                gerarTransacao(

                        investimento,

                        valor,

                        "Resgate de investimento: "
                                + investimento.getNome(),

                        "Resgate de Investimento"
                );


        movimentacao.setTransacao(
                transacao
        );


        /*
         * Se retirou exatamente todo o valor
         * existente no investimento,
         * ele é encerrado.
         */
        boolean resgateTotal =
                valor.compareTo(
                        saldoInvestimento
                ) == 0;


        if (resgateTotal) {

            investimento.setEncerrado(
                    true
            );


            investimentoRepository
                    .save(
                            investimento
                    );
        }
    }


    /* ===================================================
       RENDIMENTO
    =================================================== */

    private void registrarRendimento(

            Investimento investimento,

            InvestimentoMovimentacao movimentacao,

            LocalDate referencia

    ) {

        if (referencia == null) {

            throw new RuntimeException(
                    "Informe o mês de referência do rendimento"
            );
        }


        YearMonth mesReferencia =
                YearMonth.from(
                        referencia
                );


        YearMonth mesCriacao =
                YearMonth.from(
                        investimento.getDataCriacao()
                );


        YearMonth mesAtual =
                YearMonth.now();


        /*
         * Não pode informar rendimento
         * antes da criação do investimento.
         */
        if (
                mesReferencia.isBefore(
                        mesCriacao
                )
        ) {

            throw new RuntimeException(
                    "Não é possível registrar rendimento anterior à criação do investimento"
            );
        }


        /*
         * Também não faz sentido
         * cadastrar rendimento futuro.
         */
        if (
                mesReferencia.isAfter(
                        mesAtual
                )
        ) {

            throw new RuntimeException(
                    "Não é possível registrar rendimento para um mês futuro"
            );
        }


        LocalDateTime inicioMes =
                mesReferencia
                        .atDay(1)
                        .atStartOfDay();


        LocalDateTime fimMes =
                mesReferencia
                        .plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();


        /*
         * Somente um rendimento
         * por investimento em cada mês.
         */
        boolean jaExiste =
                movimentacaoRepository
                        .existeRendimentoNoPeriodo(

                                investimento.getId(),

                                inicioMes,

                                fimMes
                        );


        if (jaExiste) {

            throw new RuntimeException(
                    "Já existe um rendimento registrado para este investimento neste mês"
            );
        }


        /*
         * O rendimento pertence ao mês
         * informado pelo usuário.
         *
         * Gravamos no primeiro dia do mês.
         * Como os cálculos são mensais,
         * o horário não tem importância.
         */
        movimentacao.setData(
                inicioMes
        );


        /*
         * Rendimento NÃO cria transação.
         *
         * Ele não representa entrada/saída
         * de dinheiro disponível do usuário.
         */
        movimentacao.setTransacao(
                null
        );
    }


    /* ===================================================
       GERAR TRANSAÇÃO
    =================================================== */

    private Transacao gerarTransacao(

            Investimento investimento,

            BigDecimal valor,

            String descricao,

            String nomeCategoria

    ) {

        Categoria categoria =
                categoriaRepository
                        .findByNomeAndPadraoSistemaTrue(
                                nomeCategoria
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria padrão não encontrada"
                                )
                        );


        Transacao transacao =
                new Transacao();


        transacao.setDescricao(
                descricao
        );


        transacao.setValor(
                valor
        );


        transacao.setCategoria(
                categoria
        );


        transacao.setUsuario(
                investimento.getUsuario()
        );


        transacao.setStatus(
                StatusPagamento.PAGO
        );


        transacao.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );


        transacao.setData(
                LocalDate.now()
        );


        transacao.setBloqueadaSistema(
                true
        );


        return transacaoRepository
                .save(
                        transacao
                );
    }


    /* ===================================================
       LISTAR MOVIMENTAÇÕES
    =================================================== */
    public Page<InvestimentoMovimentacaoDTO> listarMovimentacoes(

            Long investimentoId,

            Integer mes,

            Integer ano,

            TipoInvestimento tipo,

            Pageable pageable

    ) {

        buscarInvestimento(
                investimentoId
        );


        /*
         * Sem mês e ano:
         * listagem completa.
         *
         * Usada atualmente para os cálculos
         * e gráfico do detalhe.
         */
        if (
                mes == null ||
                        ano == null
        ) {

            return movimentacaoRepository
                    .findByInvestimentoIdOrderByDataDesc(

                            investimentoId,

                            pageable

                    )
                    .map(
                            this::toDTO
                    );

        }


        YearMonth referencia =
                YearMonth.of(
                        ano,
                        mes
                );


        LocalDateTime inicio =
                referencia
                        .atDay(1)
                        .atStartOfDay();


        LocalDateTime fim =
                referencia
                        .plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();


        return movimentacaoRepository
                .listarMovimentacoesFiltradas(

                        investimentoId,

                        tipo,

                        inicio,

                        fim,

                        pageable

                )
                .map(
                        this::toDTO
                );

    }


    /* ===================================================
       EDITAR MOVIMENTAÇÃO
    =================================================== */

    @Transactional
    public InvestimentoMovimentacaoDTO editarMovimentacao(

            Long movimentacaoId,

            BigDecimal novoValor,

            LocalDate novaReferencia

    ) {

        InvestimentoMovimentacao movimentacao =
                movimentacaoRepository
                        .findById(
                                movimentacaoId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Movimentação não encontrada"
                                )
                        );


        Investimento investimento =
                movimentacao
                        .getInvestimento();


        validarValor(
                novoValor
        );


        /*
         * Permitimos editar registros históricos,
         * inclusive de investimento encerrado.
         *
         * Porém, se a alteração de um resgate
         * fizer o investimento voltar a ter saldo,
         * reabrimos o investimento.
         */


        /* ===================================================
           EDITAR APORTE
        =================================================== */

        if (
                movimentacao.getTipo()
                        ==
                        TipoInvestimento.APORTE
        ) {

            validarEdicaoAporte(
                    movimentacao,
                    novoValor
            );


            movimentacao.setValor(
                    novoValor
            );


            sincronizarTransacao(
                    movimentacao,
                    novoValor
            );
        }


        /* ===================================================
           EDITAR RESGATE
        =================================================== */

        else if (
                movimentacao.getTipo()
                        ==
                        TipoInvestimento.RESGATE
        ) {

            /*
             * Como o saldo atual já considera
             * o valor do resgate antigo,
             * devolvemos esse valor antes
             * de validar o novo.
             */
            BigDecimal saldoAtual =
                    investimentoService
                            .calcularSaldoAtual(
                                    investimento.getId()
                            );


            BigDecimal saldoAntesResgate =
                    saldoAtual.add(
                            movimentacao.getValor()
                    );


            if (
                    novoValor.compareTo(
                            saldoAntesResgate
                    ) > 0
            ) {

                throw new RuntimeException(
                        "Saldo insuficiente no investimento para este resgate"
                );
            }


            movimentacao.setValor(
                    novoValor
            );


            sincronizarTransacao(
                    movimentacao,
                    novoValor
            );


            boolean resgateTotal =
                    novoValor.compareTo(
                            saldoAntesResgate
                    ) == 0;


            investimento.setEncerrado(
                    resgateTotal
            );


            investimentoRepository
                    .save(
                            investimento
                    );
        }


        /* ===================================================
           EDITAR RENDIMENTO
        =================================================== */

        else if (
                movimentacao.getTipo()
                        ==
                        TipoInvestimento.RENDIMENTO
        ) {

            editarRendimento(

                    movimentacao,

                    novoValor,

                    novaReferencia
            );
        }


        InvestimentoMovimentacao salva =
                movimentacaoRepository
                        .save(
                                movimentacao
                        );


        return toDTO(
                salva
        );
    }


    /* ===================================================
       EDITAR RENDIMENTO
    =================================================== */

    private void editarRendimento(

            InvestimentoMovimentacao movimentacao,

            BigDecimal novoValor,

            LocalDate novaReferencia

    ) {

        movimentacao.setValor(
                novoValor
        );


        /*
         * Se o usuário não mudou a referência,
         * mantemos o mês atual do registro.
         */
        YearMonth mesReferencia =
                novaReferencia != null

                        ? YearMonth.from(
                        novaReferencia
                )

                        : YearMonth.from(
                        movimentacao.getData()
                );


        YearMonth mesCriacao =
                YearMonth.from(
                        movimentacao
                                .getInvestimento()
                                .getDataCriacao()
                );


        YearMonth mesAtual =
                YearMonth.now();


        if (
                mesReferencia.isBefore(
                        mesCriacao
                )
        ) {

            throw new RuntimeException(
                    "O rendimento não pode ser anterior à criação do investimento"
            );
        }


        if (
                mesReferencia.isAfter(
                        mesAtual
                )
        ) {

            throw new RuntimeException(
                    "O rendimento não pode ser movido para um mês futuro"
            );
        }


        LocalDateTime inicioMes =
                mesReferencia
                        .atDay(1)
                        .atStartOfDay();


        LocalDateTime fimMes =
                mesReferencia
                        .plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();


        boolean existeOutro =
                movimentacaoRepository
                        .existeOutroRendimentoNoPeriodo(

                                movimentacao
                                        .getInvestimento()
                                        .getId(),

                                movimentacao.getId(),

                                inicioMes,

                                fimMes
                        );


        if (existeOutro) {

            throw new RuntimeException(
                    "Já existe outro rendimento registrado para este investimento neste mês"
            );
        }


        movimentacao.setData(
                inicioMes
        );
    }


    /* ===================================================
       VALIDAR EDIÇÃO DE APORTE
    =================================================== */

    private void validarEdicaoAporte(

            InvestimentoMovimentacao movimentacao,

            BigDecimal novoValor

    ) {

        BigDecimal valorAnterior =
                movimentacao
                        .getValor();


        BigDecimal diferenca =
                novoValor.subtract(
                        valorAnterior
                );


        /*
         * Se reduziu ou manteve,
         * não precisa de dinheiro adicional.
         */
        if (
                diferenca.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {

            return;
        }


        BigDecimal saldoUsuario =
                transacaoRepository
                        .calcularSaldoUsuario(

                                movimentacao
                                        .getInvestimento()
                                        .getUsuario()
                                        .getId(),

                                StatusPagamento.PAGO
                        );


        if (saldoUsuario == null) {

            saldoUsuario =
                    BigDecimal.ZERO;
        }


        if (
                diferenca.compareTo(
                        saldoUsuario
                ) > 0
        ) {

            throw new RuntimeException(
                    "Saldo insuficiente para aumentar o aporte"
            );
        }
    }


    /* ===================================================
       SINCRONIZAR TRANSAÇÃO
    =================================================== */

    private void sincronizarTransacao(

            InvestimentoMovimentacao movimentacao,

            BigDecimal novoValor

    ) {

        Transacao transacao =
                movimentacao
                        .getTransacao();


        if (transacao == null) {

            return;
        }


        transacao.setValor(
                novoValor
        );


        transacaoRepository
                .save(
                        transacao
                );
    }


    /* ===================================================
       EXCLUIR MOVIMENTAÇÃO
    =================================================== */

    @Transactional
    public void deletarMovimentacao(
            Long movimentacaoId
    ) {

        InvestimentoMovimentacao movimentacao =
                movimentacaoRepository
                        .findById(
                                movimentacaoId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Movimentação não encontrada"
                                )
                        );


        Investimento investimento =
                movimentacao
                        .getInvestimento();


        Transacao transacao =
                movimentacao
                        .getTransacao();


        /*
         * Desvinculamos antes para evitar
         * problema de chave estrangeira.
         */
        movimentacao.setTransacao(
                null
        );


        movimentacaoRepository
                .save(
                        movimentacao
                );


        movimentacaoRepository
                .delete(
                        movimentacao
                );


        /*
         * Rendimento não tem transação,
         * então aqui simplesmente não entra.
         */
        if (transacao != null) {

            transacaoRepository
                    .delete(
                            transacao
                    );
        }


        /*
         * Depois de excluir um registro,
         * recalculamos o saldo.
         *
         * Se existir dinheiro novamente,
         * o investimento deve ficar ativo.
         */
        BigDecimal saldoAtual =
                investimentoService
                        .calcularSaldoAtual(
                                investimento.getId()
                        );


        if (
                saldoAtual.compareTo(
                        BigDecimal.ZERO
                ) > 0
        ) {

            investimento.setEncerrado(
                    false
            );


            investimentoRepository
                    .save(
                            investimento
                    );
        }

    }


    /* ===================================================
       AUXILIARES
    =================================================== */

    private Investimento buscarInvestimento(
            Long investimentoId
    ) {

        return investimentoRepository
                .findById(
                        investimentoId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Investimento não encontrado"
                        )
                );
    }


    private void validarValor(
            BigDecimal valor
    ) {

        if (
                valor == null ||
                        valor.compareTo(
                                BigDecimal.ZERO
                        ) <= 0
        ) {

            throw new RuntimeException(
                    "O valor deve ser maior que zero"
            );
        }
    }


    private InvestimentoMovimentacaoDTO toDTO(

            InvestimentoMovimentacao movimentacao

    ) {

        return new InvestimentoMovimentacaoDTO(

                movimentacao.getId(),

                movimentacao
                        .getInvestimento()
                        .getNome(),

                movimentacao.getTipo(),

                movimentacao.getValor(),

                movimentacao.getData()

        );
    }

}