package TCC.ControleFincanceiro.service;


import TCC.ControleFincanceiro.dto.dashboard.DashboardCategoriaDTO;
import TCC.ControleFincanceiro.dto.dashboard.DashboardDTO;
import TCC.ControleFincanceiro.dto.dashboard.DashboardTransacaoDTO;

import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResumoDTO;

import TCC.ControleFincanceiro.entity.PlanejamentoMensal;
import TCC.ControleFincanceiro.entity.Transacao;

import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;

import TCC.ControleFincanceiro.repository.PlanejamentoMensalRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.YearMonth;

import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DashboardService {


    private final TransacaoRepository transacaoRepository;

    private final PlanejamentoMensalRepository planejamentoRepository;

    private final PlanejamentoCategoriaService planejamentoCategoriaService;


    /* ===================================================
       DASHBOARD
    =================================================== */

    public DashboardDTO buscarDashboard(

            Long usuarioId,

            int mes,

            int ano

    ) {


        /* ===================================================
           PERÍODO SELECIONADO
        =================================================== */

        YearMonth referencia =
                YearMonth.of(
                        ano,
                        mes
                );


        LocalDate inicioMes =
                referencia.atDay(
                        1
                );


        LocalDate fimMes =
                referencia.atEndOfMonth();


        /* ===================================================
           SALDO ACUMULADO
        ===================================================

           Diferente dos outros cards,
           o saldo considera tudo que aconteceu
           até o último dia do mês selecionado.

           Dessa forma, a sobra de um mês
           passa para o próximo.
        =================================================== */

        BigDecimal saldo =
                transacaoRepository
                        .calcularSaldoUsuarioAteData(

                                usuarioId,

                                StatusPagamento.PAGO,

                                fimMes

                        );


        if (saldo == null) {

            saldo =
                    BigDecimal.ZERO;

        }


        /* ===================================================
           RECEITAS DO MÊS
        ===================================================

           Não contamos aqui receitas técnicas
           vindas de:

           - Resgate de Investimento
           - Resgate de Objetivo

           Elas afetam o saldo, mas não representam
           uma nova renda do usuário.
        =================================================== */

        BigDecimal receita =
                transacaoRepository
                        .totalReceitasDashboardNoMes(

                                usuarioId,

                                mes,

                                ano

                        );


        if (receita == null) {

            receita =
                    BigDecimal.ZERO;

        }


        /* ===================================================
           DESPESAS DO MÊS
        ===================================================

           Não entram:

           - Investimentos
           - Objetivos

           Essas categorias representam dinheiro
           guardado, não consumo normal.

           Investimentos possuem card próprio.
        =================================================== */

        BigDecimal despesa =
                transacaoRepository
                        .totalDespesasDashboardNoMes(

                                usuarioId,

                                mes,

                                ano

                        );


        if (despesa == null) {

            despesa =
                    BigDecimal.ZERO;

        }


        /* ===================================================
           INVESTIMENTOS DO MÊS
        ===================================================

           Aqui entram somente os aportes.

           Todo aporte cria uma transação com
           a categoria padrão "Investimentos".
        =================================================== */

        BigDecimal investimento =
                transacaoRepository
                        .totalCategoriaSistemaNoMes(

                                usuarioId,

                                "Investimentos",

                                StatusPagamento.PAGO,

                                mes,

                                ano

                        );


        if (investimento == null) {

            investimento =
                    BigDecimal.ZERO;

        }


        /* ===================================================
           3 CATEGORIAS MAIS CRÍTICAS
        =================================================== */

        List<DashboardCategoriaDTO> categoriasCriticas =
                buscarCategoriasCriticas(

                        usuarioId,

                        referencia

                );


        /* ===================================================
           ÚLTIMAS 5 TRANSAÇÕES
        =================================================== */

        List<DashboardTransacaoDTO> ultimasTransacoes =
                buscarUltimasTransacoes(

                        usuarioId,

                        inicioMes,

                        fimMes

                );


        /* ===================================================
           RETORNO
        =================================================== */

        return new DashboardDTO(

                saldo,

                receita,

                despesa,

                investimento,

                categoriasCriticas,

                ultimasTransacoes

        );

    }


    /* ===================================================
       CATEGORIAS CRÍTICAS
    =================================================== */

    private List<DashboardCategoriaDTO> buscarCategoriasCriticas(

            Long usuarioId,

            YearMonth referencia

    ) {


        /*
         * Procuramos o planejamento
         * correspondente exatamente
         * ao mês selecionado.
         */

        PlanejamentoMensal planejamento =
                planejamentoRepository
                        .findByUsuarioIdOrderByReferenciaDesc(
                                usuarioId
                        )
                        .stream()
                        .filter(item ->

                                YearMonth.from(
                                                item.getReferencia()
                                        )
                                        .equals(
                                                referencia
                                        )

                        )
                        .findFirst()
                        .orElse(
                                null
                        );


        /*
         * Se o usuário não possui planejamento
         * naquele mês, não existem categorias
         * críticas para mostrar.
         */

        if (planejamento == null) {

            return List.of();

        }


        List<PlanejamentoCategoriaResumoDTO> categorias =
                planejamentoCategoriaService
                        .resumo(
                                planejamento.getId()
                        );


        /*
         * O Dashboard mostra somente
         * as três categorias mais críticas.
         *
         * Quanto maior o percentual utilizado,
         * maior a prioridade.
         */

        return categorias
                .stream()

                /*
                 * "Categorias restantes" é uma linha
                 * técnica do planejamento e não uma
                 * categoria real.
                 */
                .filter(
                        categoria ->
                                categoria.categoriaId()
                                        != null
                )

                .sorted(
                        Comparator.comparing(
                                        PlanejamentoCategoriaResumoDTO
                                                ::percentualUtilizado
                                )
                                .reversed()
                )

                .limit(
                        3
                )

                .map(
                        categoria -> {


                            BigDecimal utilizado =
                                    categoria.pago()
                                            .add(
                                                    categoria.pendente()
                                            )
                                            .add(
                                                    categoria.pagoUltrapassado()
                                            )
                                            .add(
                                                    categoria.pendenteUltrapassado()
                                            );


                            return new DashboardCategoriaDTO(

                                    categoria.categoriaId(),

                                    categoria.categoria(),

                                    categoria.icone(),

                                    categoria.limite(),

                                    utilizado,

                                    categoria.percentualUtilizado(),

                                    categoria.status()

                            );

                        }
                )

                .toList();

    }


    /* ===================================================
       ÚLTIMAS TRANSAÇÕES
    =================================================== */

    private List<DashboardTransacaoDTO> buscarUltimasTransacoes(

            Long usuarioId,

            LocalDate inicioMes,

            LocalDate fimMes

    ) {


        /*
         * Página zero com tamanho cinco:
         *
         * somente as cinco movimentações
         * mais recentes do mês.
         */

        Page<Transacao> pagina =
                transacaoRepository
                        .findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(

                                usuarioId,

                                inicioMes,

                                fimMes,

                                PageRequest.of(
                                        0,
                                        5
                                )

                        );


        return pagina
                .getContent()
                .stream()
                .map(
                        transacao -> {

                            String tipo =
                                    transacao
                                            .getCategoria()
                                            .getTipo()
                                            .name();


                            return new DashboardTransacaoDTO(

                                    transacao.getId(),

                                    transacao.getDescricao(),

                                    transacao.getValor(),

                                    transacao.getData(),

                                    tipo,

                                    transacao
                                            .getCategoria()
                                            .getNome(),

                                    transacao
                                            .getCategoria()
                                            .getIcone()

                            );

                        }
                )
                .toList();

    }

}