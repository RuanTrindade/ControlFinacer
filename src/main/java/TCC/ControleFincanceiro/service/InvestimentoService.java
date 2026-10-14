package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.investimento.CarteiraInvestimentoResumoDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoAtualizarDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoCardDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoCriarDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoEstatisticaDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoResumoDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoResumoMensalDTO;

import TCC.ControleFincanceiro.entity.Investimento;
import TCC.ControleFincanceiro.entity.Usuario;

import TCC.ControleFincanceiro.entity.enumerated.TipoAtivo;
import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;

import TCC.ControleFincanceiro.repository.InvestimentoMovimentacaoRepository;
import TCC.ControleFincanceiro.repository.InvestimentoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InvestimentoService {


    private final InvestimentoRepository investimentoRepository;

    private final UsuarioRepository usuarioRepository;

    private final InvestimentoMovimentacaoRepository movimentacaoRepository;


    /* ===================================================
       CRIAR INVESTIMENTO
    =================================================== */

    public InvestimentoResumoDTO criarInvestimento(
            InvestimentoCriarDTO dto
    ) {

        Usuario usuario =
                usuarioRepository
                        .findById(
                                dto.usuarioId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuário não encontrado"
                                )
                        );


        if (
                dto.nome() == null ||
                        dto.nome().isBlank()
        ) {

            throw new RuntimeException(
                    "O nome do investimento é obrigatório"
            );
        }


        TipoAtivo tipo;

        try {

            tipo =
                    TipoAtivo.valueOf(
                            dto.tipo()
                    );

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Tipo de investimento inválido"
            );
        }


        Investimento investimento =
                new Investimento();


        investimento.setNome(
                dto.nome().trim()
        );


        investimento.setTipo(
                tipo
        );


        investimento.setDataCriacao(
                LocalDate.now()
        );


        investimento.setEncerrado(
                false
        );


        investimento.setUsuario(
                usuario
        );


        Investimento salvo =
                investimentoRepository.save(
                        investimento
                );


        return toResumoDTO(
                salvo
        );
    }


    /* ===================================================
       LISTAGEM SIMPLES
    =================================================== */

    public List<InvestimentoResumoDTO> listarInvestimentos(
            Long usuarioId
    ) {

        return investimentoRepository
                .findByUsuarioId(
                        usuarioId
                )
                .stream()
                .map(
                        this::toResumoDTO
                )
                .toList();
    }


    /* ===================================================
       BUSCAR POR ID
    =================================================== */

    public InvestimentoResumoDTO buscarPorId(
            Long investimentoId
    ) {

        Investimento investimento =
                buscarInvestimento(
                        investimentoId
                );


        return toResumoDTO(
                investimento
        );
    }


    /* ===================================================
       ATUALIZAR INVESTIMENTO
    =================================================== */

    public InvestimentoResumoDTO atualizarInvestimento(

            Long investimentoId,

            Long usuarioId,

            InvestimentoAtualizarDTO dto

    ) {

        Investimento investimento =
                buscarInvestimento(
                        investimentoId
                );


        if (
                !investimento
                        .getUsuario()
                        .getId()
                        .equals(
                                usuarioId
                        )
        ) {

            throw new RuntimeException(
                    "Acesso negado"
            );
        }


        if (
                Boolean.TRUE.equals(
                        investimento.getEncerrado()
                )
        ) {

            throw new RuntimeException(
                    "Investimentos encerrados não podem ser alterados"
            );
        }


        if (
                dto.nome() == null ||
                        dto.nome().isBlank()
        ) {

            throw new RuntimeException(
                    "O nome do investimento é obrigatório"
            );
        }


        TipoAtivo tipo;

        try {

            tipo =
                    TipoAtivo.valueOf(
                            dto.tipo()
                    );

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Tipo de investimento inválido"
            );
        }


        investimento.setNome(
                dto.nome().trim()
        );


        investimento.setTipo(
                tipo
        );


        Investimento salvo =
                investimentoRepository.save(
                        investimento
                );


        return toResumoDTO(
                salvo
        );
    }


    /* ===================================================
       EXCLUIR INVESTIMENTO
    =================================================== */

    public void deletarInvestimento(

            Long investimentoId,

            Long usuarioId

    ) {

        Investimento investimento =
                buscarInvestimento(
                        investimentoId
                );


        if (
                !investimento
                        .getUsuario()
                        .getId()
                        .equals(
                                usuarioId
                        )
        ) {

            throw new RuntimeException(
                    "Acesso negado"
            );
        }


        /*
         * Investimento encerrado faz parte
         * do histórico e não deve ser apagado.
         */
        if (
                Boolean.TRUE.equals(
                        investimento.getEncerrado()
                )
        ) {

            throw new RuntimeException(
                    "Investimentos encerrados não podem ser excluídos"
            );
        }


        /*
         * Se já teve aporte, resgate ou rendimento,
         * preservamos o histórico.
         */
        boolean possuiMovimentacoes =
                movimentacaoRepository
                        .existsByInvestimentoId(
                                investimentoId
                        );


        if (possuiMovimentacoes) {

            throw new RuntimeException(
                    "Este investimento possui histórico e não pode ser excluído"
            );
        }


        investimentoRepository.delete(
                investimento
        );
    }


    /* ===================================================
       ESTATÍSTICAS GERAIS / ATUAIS
    =================================================== */

    public InvestimentoEstatisticaDTO obterEstatisticas(
            Long investimentoId
    ) {

        Investimento investimento =
                buscarInvestimento(
                        investimentoId
                );


        BigDecimal aportes =
                valorOuZero(
                        movimentacaoRepository
                                .totalAportes(
                                        investimentoId
                                )
                );


        BigDecimal resgates =
                valorOuZero(
                        movimentacaoRepository
                                .totalResgates(
                                        investimentoId
                                )
                );


        BigDecimal rendimentos =
                valorOuZero(
                        movimentacaoRepository
                                .totalRendimentos(
                                        investimentoId
                                )
                );


        BigDecimal saldoAtual =
                aportes
                        .subtract(
                                resgates
                        )
                        .add(
                                rendimentos
                        );


        return new InvestimentoEstatisticaDTO(

                investimento.getId(),

                investimento.getNome(),

                aportes,

                resgates,

                rendimentos,

                saldoAtual

        );
    }


    /* ===================================================
       SALDO ATUAL REAL
    ===================================================

       Todos os registros existentes:

       aportes
       - resgates
       + rendimentos
    =================================================== */

    public BigDecimal calcularSaldoAtual(
            Long investimentoId
    ) {

        buscarInvestimento(
                investimentoId
        );


        BigDecimal aportes =
                valorOuZero(
                        movimentacaoRepository
                                .totalAportes(
                                        investimentoId
                                )
                );


        BigDecimal resgates =
                valorOuZero(
                        movimentacaoRepository
                                .totalResgates(
                                        investimentoId
                                )
                );


        BigDecimal rendimentos =
                valorOuZero(
                        movimentacaoRepository
                                .totalRendimentos(
                                        investimentoId
                                )
                );


        return aportes
                .subtract(
                        resgates
                )
                .add(
                        rendimentos
                );
    }


    /* ===================================================
       SALDO ACUMULADO ATÉ O MÊS
    =================================================== */

    public BigDecimal calcularSaldoAteMes(

            Long investimentoId,

            Integer mes,

            Integer ano

    ) {

        validarMesAno(
                mes,
                ano
        );


        LocalDateTime fim =
                obterFimPeriodo(
                        mes,
                        ano
                );


        BigDecimal aportes =
                valorOuZero(
                        movimentacaoRepository
                                .totalPorTipoAtePeriodo(

                                        investimentoId,

                                        TipoInvestimento.APORTE,

                                        fim
                                )
                );


        BigDecimal resgates =
                valorOuZero(
                        movimentacaoRepository
                                .totalPorTipoAtePeriodo(

                                        investimentoId,

                                        TipoInvestimento.RESGATE,

                                        fim
                                )
                );


        BigDecimal rendimentos =
                valorOuZero(
                        movimentacaoRepository
                                .totalPorTipoAtePeriodo(

                                        investimentoId,

                                        TipoInvestimento.RENDIMENTO,

                                        fim
                                )
                );


        return aportes
                .subtract(
                        resgates
                )
                .add(
                        rendimentos
                );
    }


    /* ===================================================
       RENDIMENTO ACUMULADO ATÉ O MÊS
    =================================================== */

    public BigDecimal calcularRendimentoAteMes(

            Long investimentoId,

            Integer mes,

            Integer ano

    ) {

        validarMesAno(
                mes,
                ano
        );


        LocalDateTime fim =
                obterFimPeriodo(
                        mes,
                        ano
                );


        return valorOuZero(
                movimentacaoRepository
                        .totalPorTipoAtePeriodo(

                                investimentoId,

                                TipoInvestimento.RENDIMENTO,

                                fim
                        )
        );
    }


    /* ===================================================
       RESUMO DE UM INVESTIMENTO NO MÊS
    ===================================================

       Aporte:
       somente do mês selecionado.

       Resgate:
       somente do mês selecionado.

       Valor:
       acumulado até aquele mês.

       Rendimento:
       acumulado até aquele mês.
    =================================================== */

    public InvestimentoResumoMensalDTO obterResumoMensal(

            Long investimentoId,

            Integer mes,

            Integer ano

    ) {

        Investimento investimento =
                buscarInvestimento(
                        investimentoId
                );


        validarMesAno(
                mes,
                ano
        );


        YearMonth anoMes =
                YearMonth.of(
                        ano,
                        mes
                );


        LocalDate referencia =
                anoMes.atDay(1);


        LocalDateTime inicioMes =
                referencia
                        .atStartOfDay();


        LocalDateTime fimMes =
                anoMes
                        .plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();


        /*
         * Somente movimentações realizadas
         * dentro daquele mês.
         */
        BigDecimal aportesMes =
                valorOuZero(
                        movimentacaoRepository
                                .totalPorTipoPeriodo(

                                        investimentoId,

                                        TipoInvestimento.APORTE,

                                        inicioMes,

                                        fimMes
                                )
                );


        BigDecimal resgatesMes =
                valorOuZero(
                        movimentacaoRepository
                                .totalPorTipoPeriodo(

                                        investimentoId,

                                        TipoInvestimento.RESGATE,

                                        inicioMes,

                                        fimMes
                                )
                );


        /*
         * Estes dois são acumulados
         * desde a criação até o mês escolhido.
         */
        BigDecimal valorAteMes =
                calcularSaldoAteMes(

                        investimentoId,

                        mes,

                        ano
                );


        BigDecimal rendimentoAteMes =
                calcularRendimentoAteMes(

                        investimentoId,

                        mes,

                        ano
                );


        /*
         * Estamos mantendo temporariamente
         * o DTO antigo para evitar mudar
         * todo o projeto de uma vez.
         *
         * Significado atual:
         *
         * valorNoMes = saldo acumulado até o mês
         * rendimentoMes = rendimento acumulado até o mês
         *
         * Os campos antigos de atualização
         * não têm mais função.
         */
        return new InvestimentoResumoMensalDTO(

                investimento.getId(),

                investimento.getNome(),

                referencia,

                valorAteMes,

                aportesMes,

                resgatesMes,

                rendimentoAteMes,

                null,

                true,

                null

        );
    }


    /* ===================================================
       RESUMO DA CARTEIRA
    =================================================== */

    public CarteiraInvestimentoResumoDTO obterResumoCarteira(

            Long usuarioId,

            Integer mes,

            Integer ano

    ) {

        validarMesAno(
                mes,
                ano
        );


        List<Investimento> investimentos =
                investimentoRepository
                        .findByUsuarioId(
                                usuarioId
                        );


        BigDecimal valorCarteira =
                BigDecimal.ZERO;


        BigDecimal aportesMes =
                BigDecimal.ZERO;


        BigDecimal resgatesMes =
                BigDecimal.ZERO;


        BigDecimal rendimentosAcumulados =
                BigDecimal.ZERO;


        long investimentosAtivos =
                0;


        long pendentesAtualizacao =
                0;


        for (
                Investimento investimento :
                investimentos
        ) {


            /*
             * Se o investimento ainda nem existia
             * no mês selecionado, ele não entra
             * nos cálculos daquele período.
             */
            YearMonth mesCriacao =
                    YearMonth.from(
                            investimento.getDataCriacao()
                    );


            YearMonth mesSelecionado =
                    YearMonth.of(
                            ano,
                            mes
                    );


            if (
                    mesCriacao.isAfter(
                            mesSelecionado
                    )
            ) {

                continue;
            }


            InvestimentoResumoMensalDTO resumo =
                    obterResumoMensal(

                            investimento.getId(),

                            mes,

                            ano
                    );


            /*
             * Valor acumulado da carteira.
             */
            if (
                    resumo.valorNoMes() != null
            ) {

                valorCarteira =
                        valorCarteira.add(
                                resumo.valorNoMes()
                        );
            }


            /*
             * Somente aportes daquele mês.
             */
            if (
                    resumo.totalAportesMes() != null
            ) {

                aportesMes =
                        aportesMes.add(
                                resumo.totalAportesMes()
                        );
            }


            /*
             * Somente resgates daquele mês.
             */
            if (
                    resumo.totalResgatesMes() != null
            ) {

                resgatesMes =
                        resgatesMes.add(
                                resumo.totalResgatesMes()
                        );
            }


            /*
             * Rendimento acumulado
             * até o mês selecionado.
             */
            if (
                    resumo.rendimentoMes() != null
            ) {

                rendimentosAcumulados =
                        rendimentosAcumulados.add(
                                resumo.rendimentoMes()
                        );
            }


            if (
                    !Boolean.TRUE.equals(
                            investimento.getEncerrado()
                    )
            ) {

                investimentosAtivos++;


                boolean pendente =
                        !movimentacaoRepository
                                .existeRendimentoNoPeriodo(

                                        investimento.getId(),

                                        obterInicioPeriodo(
                                                mes,
                                                ano
                                        ),

                                        obterFimPeriodo(
                                                mes,
                                                ano
                                        )
                                );


                if (pendente) {

                    pendentesAtualizacao++;

                }

            }

        }


        return new CarteiraInvestimentoResumoDTO(

                valorCarteira,

                aportesMes,

                resgatesMes,

                rendimentosAcumulados,

                investimentosAtivos,

                pendentesAtualizacao

        );
    }


    /* ===================================================
       CARDS DOS INVESTIMENTOS
    =================================================== */

    public Page<InvestimentoCardDTO> listarInvestimentos(

            Long usuarioId,

            Boolean encerrado,

            Integer mes,

            Integer ano,

            Pageable pageable

    ) {

        validarMesAno(
                mes,
                ano
        );


        YearMonth mesSelecionado =
                YearMonth.of(
                        ano,
                        mes
                );


        return investimentoRepository
                .findByUsuarioIdAndEncerrado(

                        usuarioId,

                        encerrado,

                        pageable
                )
                .map(
                        investimento -> {


                            /*
                             * O repository ainda traz os
                             * investimentos normalmente.
                             *
                             * Enquanto não alteramos a consulta
                             * paginada, investimentos criados
                             * depois do mês escolhido ficam
                             * com valor zero.
                             *
                             * Depois ajustaremos isso no repository
                             * para nem aparecerem.
                             */
                            YearMonth mesCriacao =
                                    YearMonth.from(
                                            investimento.getDataCriacao()
                                    );


                            BigDecimal valorAteMes =
                                    BigDecimal.ZERO;


                            BigDecimal rendimentoAteMes =
                                    BigDecimal.ZERO;


                            boolean pendenteAtualizacao =
                                    false;


                            if (
                                    !mesCriacao.isAfter(
                                            mesSelecionado
                                    )
                            ) {

                                valorAteMes =
                                        calcularSaldoAteMes(

                                                investimento.getId(),

                                                mes,

                                                ano
                                        );


                                rendimentoAteMes =
                                        calcularRendimentoAteMes(

                                                investimento.getId(),

                                                mes,

                                                ano
                                        );


                                /*
                                 * Pendente de atualização quando
                                 * ainda não existe rendimento
                                 * registrado para este mês.
                                 */
                                pendenteAtualizacao =
                                        !movimentacaoRepository
                                                .existeRendimentoNoPeriodo(

                                                        investimento.getId(),

                                                        obterInicioPeriodo(
                                                                mes,
                                                                ano
                                                        ),

                                                        obterFimPeriodo(
                                                                mes,
                                                                ano
                                                        )
                                                );

                            }


                            return new InvestimentoCardDTO(

                                    investimento.getId(),

                                    investimento.getNome(),

                                    investimento.getTipo(),

                                    investimento.getDataCriacao(),

                                    investimento.getEncerrado(),

                                    pendenteAtualizacao,

                                    valorAteMes,

                                    rendimentoAteMes

                            );

                        }
                );
    }


    /* ===================================================
       SALDO ATUAL DA CARTEIRA
    =================================================== */

    public BigDecimal obterSaldoAtualCarteira(
            Long usuarioId
    ) {

        List<Investimento> investimentos =
                investimentoRepository
                        .findByUsuarioId(
                                usuarioId
                        );


        BigDecimal saldoTotal =
                BigDecimal.ZERO;


        for (
                Investimento investimento :
                investimentos
        ) {

            if (
                    Boolean.TRUE.equals(
                            investimento.getEncerrado()
                    )
            ) {

                continue;

            }


            saldoTotal =
                    saldoTotal.add(
                            calcularSaldoAtual(
                                    investimento.getId()
                            )
                    );
        }


        return saldoTotal;
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


    private BigDecimal valorOuZero(
            BigDecimal valor
    ) {

        return valor != null
                ? valor
                : BigDecimal.ZERO;
    }


    private void validarMesAno(

            Integer mes,

            Integer ano

    ) {

        if (
                mes == null ||
                        mes < 1 ||
                        mes > 12
        ) {

            throw new RuntimeException(
                    "Mês inválido"
            );
        }


        if (ano == null) {

            throw new RuntimeException(
                    "Ano inválido"
            );
        }

    }


    private LocalDateTime obterFimPeriodo(

            Integer mes,

            Integer ano

    ) {

        return YearMonth
                .of(
                        ano,
                        mes
                )
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();
    }


    private LocalDateTime obterInicioPeriodo(

            Integer mes,

            Integer ano

    ) {

        return YearMonth
                .of(
                        ano,
                        mes
                )
                .atDay(1)
                .atStartOfDay();
    }


    private InvestimentoResumoDTO toResumoDTO(
            Investimento investimento
    ) {

        /*
         * O DTO ainda possui pendenteAtualizacao
         * porque ainda não limpamos o contrato.
         *
         * Como esse conceito foi removido,
         * sempre devolvemos false.
         */
        return new InvestimentoResumoDTO(

                investimento.getId(),

                investimento.getNome(),

                investimento.getTipo(),

                investimento.getDataCriacao(),

                investimento.getEncerrado(),

                false

        );
    }

}