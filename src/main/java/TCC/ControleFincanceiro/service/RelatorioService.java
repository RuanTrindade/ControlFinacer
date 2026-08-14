package TCC.ControleFincanceiro.service;


import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResumoDTO;

import TCC.ControleFincanceiro.dto.relatorio.RelatorioCategoriaDTO;
import TCC.ControleFincanceiro.dto.relatorio.RelatorioDTO;
import TCC.ControleFincanceiro.dto.relatorio.RelatorioMensalDTO;

import TCC.ControleFincanceiro.entity.PlanejamentoMensal;

import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;

import TCC.ControleFincanceiro.repository.PlanejamentoMensalRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;

import java.time.YearMonth;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RelatorioService {


    private final TransacaoRepository transacaoRepository;

    private final PlanejamentoMensalRepository planejamentoRepository;

    private final PlanejamentoCategoriaService planejamentoCategoriaService;


    /* ===================================================
       GERAR RELATÓRIO
    =================================================== */

    public RelatorioDTO gerarRelatorio(

            Long usuarioId,

            int mes,

            int ano

    ) {


        YearMonth referenciaFinal =
                YearMonth.of(
                        ano,
                        mes
                );


        /* ===================================================
           HISTÓRICO DOS ÚLTIMOS 6 MESES
        =================================================== */

        List<RelatorioMensalDTO> historico =
                new ArrayList<>();


        /*
         * Começamos cinco meses antes
         * e terminamos exatamente no mês
         * selecionado pela Topbar.
         */

        for (
                int i = 5;
                i >= 0;
                i--
        ) {


            YearMonth referencia =
                    referenciaFinal.minusMonths(
                            i
                    );


            int mesReferencia =
                    referencia.getMonthValue();


            int anoReferencia =
                    referencia.getYear();


            /* ===================================================
               RECEITA
            ===================================================

               Reaproveitamos a mesma regra do Dashboard:

               - somente receitas normais
               - resgates internos não entram como nova renda
            =================================================== */

            BigDecimal receita =
                    transacaoRepository
                            .totalReceitasDashboardNoMes(

                                    usuarioId,

                                    mesReferencia,

                                    anoReferencia

                            );


            if (receita == null) {

                receita =
                        BigDecimal.ZERO;

            }


            /* ===================================================
               DESPESA
            ===================================================

               Mesma regra do Dashboard:

               - despesas normais
               - investimentos não entram
               - objetivos não entram
            =================================================== */

            BigDecimal despesa =
                    transacaoRepository
                            .totalDespesasDashboardNoMes(

                                    usuarioId,

                                    mesReferencia,

                                    anoReferencia

                            );


            if (despesa == null) {

                despesa =
                        BigDecimal.ZERO;

            }


            /* ===================================================
               INVESTIMENTO
            ===================================================

               Os aportes continuam sendo transações
               do tipo DESPESA.

               Porém, no relatório, separamos pela
               categoria padrão "Investimentos".
            =================================================== */

            BigDecimal investimento =
                    transacaoRepository
                            .totalCategoriaSistemaNoMes(

                                    usuarioId,

                                    "Investimentos",

                                    StatusPagamento.PAGO,

                                    mesReferencia,

                                    anoReferencia

                            );


            if (investimento == null) {

                investimento =
                        BigDecimal.ZERO;

            }


            /* ===================================================
               ADICIONAR MÊS
            =================================================== */

            String mesFormatado =
                    String.format(

                            "%02d/%d",

                            mesReferencia,

                            anoReferencia

                    );


            historico.add(

                    new RelatorioMensalDTO(

                            mesFormatado,

                            receita,

                            despesa,

                            investimento

                    )

            );

        }


        /* ===================================================
           TOTAIS DOS 6 MESES
        =================================================== */

        BigDecimal receitaTotal =
                historico
                        .stream()
                        .map(
                                RelatorioMensalDTO::receita
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal despesaTotal =
                historico
                        .stream()
                        .map(
                                RelatorioMensalDTO::despesa
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal investimentoTotal =
                historico
                        .stream()
                        .map(
                                RelatorioMensalDTO::investimento
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        /* ===================================================
           CATEGORIAS DO PLANEJAMENTO DO MÊS
        =================================================== */

        List<RelatorioCategoriaDTO> categoriasPlanejamento =
                buscarCategoriasPlanejamento(

                        usuarioId,

                        referenciaFinal

                );


        /* ===================================================
           RETORNO
        =================================================== */

        return new RelatorioDTO(

                historico,

                categoriasPlanejamento,

                receitaTotal,

                despesaTotal,

                investimentoTotal

        );

    }


    /* ===================================================
       CATEGORIAS DO PLANEJAMENTO
    =================================================== */

    private List<RelatorioCategoriaDTO>
    buscarCategoriasPlanejamento(

            Long usuarioId,

            YearMonth referencia

    ) {


        /*
         * Procuramos exatamente o planejamento
         * referente ao mês selecionado.
         */

        PlanejamentoMensal planejamento =
                planejamentoRepository
                        .findByUsuarioIdOrderByReferenciaDesc(
                                usuarioId
                        )
                        .stream()
                        .filter(
                                item ->

                                        YearMonth
                                                .from(
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
         * Se o mês não possui planejamento,
         * o gráfico de rosca receberá
         * uma lista vazia.
         */

        if (planejamento == null) {

            return List.of();

        }


        List<PlanejamentoCategoriaResumoDTO> categorias =
                planejamentoCategoriaService
                        .resumo(
                                planejamento.getId()
                        );


        return categorias
                .stream()

                /*
                 * Não mostramos
                 * "Categorias restantes"
                 * no donut.
                 */
                .filter(
                        categoria ->
                                categoria.categoriaId()
                                        != null
                )

                .map(
                        categoria -> {


                            /*
                             * Quanto daquela categoria
                             * já está comprometido.
                             *
                             * Entram:
                             *
                             * - pago
                             * - pendente
                             * - pago ultrapassado
                             * - pendente ultrapassado
                             */

                            BigDecimal utilizado =
                                    categoria
                                            .pago()
                                            .add(
                                                    categoria.pendente()
                                            )
                                            .add(
                                                    categoria.pagoUltrapassado()
                                            )
                                            .add(
                                                    categoria.pendenteUltrapassado()
                                            );


                            return new RelatorioCategoriaDTO(

                                    categoria.categoriaId(),

                                    categoria.categoria(),

                                    categoria.icone(),

                                    categoria.cor(),

                                    categoria.limite(),

                                    utilizado,

                                    categoria.percentualUtilizado()

                            );

                        }
                )

                .toList();

    }

}