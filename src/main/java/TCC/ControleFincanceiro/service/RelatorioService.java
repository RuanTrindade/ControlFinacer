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



        List<RelatorioMensalDTO> historico =
                new ArrayList<>();




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



        List<RelatorioCategoriaDTO> categoriasPlanejamento =
                buscarCategoriasPlanejamento(

                        usuarioId,

                        referenciaFinal

                );




        return new RelatorioDTO(

                historico,

                categoriasPlanejamento,

                receitaTotal,

                despesaTotal,

                investimentoTotal

        );

    }



    private List<RelatorioCategoriaDTO>
    buscarCategoriasPlanejamento(

            Long usuarioId,

            YearMonth referencia

    ) {




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


                .filter(
                        categoria ->
                                categoria.categoriaId()
                                        != null
                )

                .map(
                        categoria -> {



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