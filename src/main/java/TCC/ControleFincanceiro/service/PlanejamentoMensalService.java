package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.planejamento.*;
import TCC.ControleFincanceiro.entity.PlanejamentoCategoria;
import TCC.ControleFincanceiro.entity.PlanejamentoMensal;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.repository.PlanejamentoCategoriaRepository;
import TCC.ControleFincanceiro.repository.PlanejamentoMensalRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanejamentoMensalService {

    private final PlanejamentoMensalRepository planejamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanejamentoCategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    private final PlanejamentoCategoriaService planejamentoCategoriaService;


    public PlanejamentoResumoDTO criar(
            PlanejamentoCriarDTO dto
    ) {

        LocalDate referencia =
                dto.referencia().withDayOfMonth(1);

        if (planejamentoRepository
                .findByUsuarioIdAndReferencia(
                        dto.usuarioId(),
                        referencia
                )
                .isPresent()) {

            throw new RuntimeException(
                    "Já existe planejamento para esse mês"
            );
        }


        if (
                dto.rendaMensal() == null ||
                        dto.rendaMensal()
                                .compareTo(BigDecimal.ZERO) <= 0
        ) {
            throw new RuntimeException(
                    "A renda mensal deve ser maior que zero"
            );
        }

        if (
                dto.percentualEconomia() == null ||
                        dto.percentualEconomia()
                                .compareTo(BigDecimal.ZERO) < 0 ||
                        dto.percentualEconomia()
                                .compareTo(BigDecimal.valueOf(100)) > 0
        ) {
            throw new RuntimeException(
                    "O percentual de economia deve estar entre 0 e 100"
            );
        }


        Usuario usuario =
                usuarioRepository.findById(dto.usuarioId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuário não encontrado"
                                ));


        /*
         * Calcula quanto do planejamento fica
         * disponível inicialmente.
         *
         * Ex:
         * renda = 2200
         * economia = 20%
         *
         * metaEconomia = 440
         * valorPlanejado = 1760
         */
        BigDecimal metaEconomia =
                dto.rendaMensal()
                        .multiply(
                                dto.percentualEconomia()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );


        BigDecimal valorPlanejado =
                dto.rendaMensal()
                        .subtract(
                                metaEconomia
                        );


        PlanejamentoMensal planejamento =
                new PlanejamentoMensal();

        planejamento.setUsuario(
                usuario
        );

        planejamento.setReferencia(
                referencia
        );

        planejamento.setRendaMensal(
                dto.rendaMensal()
        );

        planejamento.setPercentualEconomia(
                dto.percentualEconomia()
        );

        planejamento.setValorPlanejado(
                valorPlanejado
        );


        PlanejamentoMensal salvo =
                planejamentoRepository.save(
                        planejamento
                );

        return toDTO(salvo);
    }


    public List<PlanejamentoResumoDTO> listar(
            Long usuarioId
    ) {

        return planejamentoRepository
                .findByUsuarioIdOrderByReferenciaDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }



    public PlanejamentoResumoDTO buscarPorId(
            Long id
    ) {

        PlanejamentoMensal planejamento =
                planejamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Planejamento não encontrado"
                                ));

        return toDTO(planejamento);
    }



    public PlanejamentoResumoDTO atualizar(
            Long id,
            PlanejamentoAtualizarDTO dto
    ) {

        PlanejamentoMensal planejamento =
                planejamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Planejamento não encontrado"
                                ));

        if (!planejamento.getUsuario()
                .getId()
                .equals(dto.usuarioId())) {

            throw new RuntimeException("Acesso negado");
        }

        if (
                dto.rendaMensal() == null ||
                        dto.rendaMensal()
                                .compareTo(BigDecimal.ZERO) <= 0
        ) {
            throw new RuntimeException(
                    "A renda mensal deve ser maior que zero"
            );
        }

        if (
                dto.percentualEconomia() == null ||
                        dto.percentualEconomia()
                                .compareTo(BigDecimal.ZERO) < 0 ||
                        dto.percentualEconomia()
                                .compareTo(BigDecimal.valueOf(100)) > 0
        ) {
            throw new RuntimeException(
                    "O percentual de economia deve estar entre 0 e 100"
            );
        }

        planejamento.setRendaMensal(dto.rendaMensal());
        planejamento.setPercentualEconomia(
                dto.percentualEconomia()
        );

        PlanejamentoMensal atualizado =
                planejamentoRepository.save(planejamento);

        return toDTO(atualizado);
    }



    @Transactional
    public PlanejamentoResumoDTO atualizarValorPlanejado(
            Long id,
            PlanejamentoValorAtualizarDTO dto
    ) {

        PlanejamentoMensal planejamento =
                planejamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Planejamento não encontrado"
                                )
                        );


        /*
         * Segurança:
         * o planejamento precisa pertencer
         * ao usuário informado.
         */
        if (
                !planejamento
                        .getUsuario()
                        .getId()
                        .equals(
                                dto.usuarioId()
                        )
        ) {

            throw new RuntimeException(
                    "Acesso negado"
            );
        }


        /*
         * O novo valor precisa ser válido.
         */
        if (
                dto.valorPlanejado() == null ||
                        dto.valorPlanejado()
                                .compareTo(
                                        BigDecimal.ZERO
                                ) <= 0
        ) {

            throw new RuntimeException(
                    "O valor do planejamento deve ser maior que zero"
            );
        }


        /*
         * Soma SOMENTE as categorias
         * realmente planejadas.
         *
         * Categorias restantes NÃO entra aqui.
         */
        BigDecimal somaCategorias =
                categoriaRepository.somaLimites(
                        id
                );


        if (somaCategorias == null) {

            somaCategorias =
                    BigDecimal.ZERO;

        }


        /*
         * O usuário pode diminuir
         * valorPlanejado.
         *
         * Porém nunca abaixo da soma
         * das categorias planejadas.
         */
        if (
                dto.valorPlanejado()
                        .compareTo(
                                somaCategorias
                        ) < 0
        ) {

            throw new RuntimeException(
                    "O valor do planejamento não pode ser menor que R$ "
                            + somaCategorias
                            + ", que é o total já distribuído nas categorias."
            );
        }


        planejamento.setValorPlanejado(
                dto.valorPlanejado()
        );


        PlanejamentoMensal atualizado =
                planejamentoRepository.save(
                        planejamento
                );


        return toDTO(
                atualizado
        );
    }



    public void deletar(
            Long id,
            Long usuarioId
    ) {

        PlanejamentoMensal planejamento =
                planejamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Planejamento não encontrado"
                                ));

        if (!planejamento.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new RuntimeException("Acesso negado");
        }

        planejamentoRepository.delete(planejamento);
    }



    @Transactional
    public PlanejamentoResumoDTO copiar(
            Long id,
            LocalDate novaReferencia
    ) {

        novaReferencia =
                novaReferencia.withDayOfMonth(1);

        PlanejamentoMensal original =
                planejamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Planejamento não encontrado"
                                ));

        if (planejamentoRepository
                .findByUsuarioIdAndReferencia(
                        original.getUsuario().getId(),
                        novaReferencia
                )
                .isPresent()) {

            throw new RuntimeException(
                    "Já existe planejamento para esse mês"
            );
        }

        PlanejamentoMensal novo =
                new PlanejamentoMensal();

        novo.setUsuario(original.getUsuario());
        novo.setReferencia(novaReferencia);
        novo.setRendaMensal(original.getRendaMensal());
        novo.setPercentualEconomia(
                original.getPercentualEconomia()
        );

        novo.setValorPlanejado(
                original.getValorPlanejado()
        );

        planejamentoRepository.save(novo);



        List<PlanejamentoCategoria> categorias =
                categoriaRepository.findByPlanejamentoMensalId(
                        original.getId()
                );

        for (PlanejamentoCategoria categoria : categorias) {

            PlanejamentoCategoria nova =
                    new PlanejamentoCategoria();

            nova.setPlanejamentoMensal(novo);
            nova.setCategoria(categoria.getCategoria());
            nova.setLimite(categoria.getLimite());

            categoriaRepository.save(nova);
        }

        return toDTO(novo);
    }



    public PlanejamentoMensalResumoDTO resumoMensal(
            Long planejamentoId
    ) {

        PlanejamentoMensal planejamento =
                planejamentoRepository.findById(planejamentoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Planejamento não encontrado"
                                ));

        int mes =
                planejamento.getReferencia().getMonthValue();

        int ano =
                planejamento.getReferencia().getYear();

        Long usuarioId =
                planejamento.getUsuario().getId();

        BigDecimal receitasMes =
                transacaoRepository
                        .totalReceitasNoMes(
                                usuarioId,
                                mes,
                                ano
                        );

        if (receitasMes == null) {
            receitasMes = BigDecimal.ZERO;
        }

        BigDecimal despesasPlanejadasPagas =
                transacaoRepository
                        .totalDespesasPlanejadasPorStatus(
                                usuarioId,
                                planejamentoId,
                                StatusPagamento.PAGO,
                                mes,
                                ano
                        );

        BigDecimal despesasPlanejadasPendentes =
                transacaoRepository
                        .totalDespesasPlanejadasPorStatus(
                                usuarioId,
                                planejamentoId,
                                StatusPagamento.PENDENTE,
                                mes,
                                ano
                        );


        BigDecimal despesasNaoPlanejadasPagas =
                transacaoRepository
                        .totalDespesasNaoPlanejadasPorStatus(
                                usuarioId,
                                planejamentoId,
                                StatusPagamento.PAGO,
                                mes,
                                ano
                        );

        BigDecimal despesasNaoPlanejadasPendentes =
                transacaoRepository
                        .totalDespesasNaoPlanejadasPorStatus(
                                usuarioId,
                                planejamentoId,
                                StatusPagamento.PENDENTE,
                                mes,
                                ano
                        );


        BigDecimal gastoTotal =
                despesasPlanejadasPagas
                        .add(
                                despesasPlanejadasPendentes
                        )
                        .add(
                                despesasNaoPlanejadasPagas
                        )
                        .add(
                                despesasNaoPlanejadasPendentes
                        );

        if (gastoTotal == null) {
            gastoTotal = BigDecimal.ZERO;
        }

        BigDecimal renda =
                planejamento.getRendaMensal();

        BigDecimal metaEconomia =
                renda
                        .multiply(
                                planejamento
                                        .getPercentualEconomia()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal valorDisponivel =
                planejamento.getValorPlanejado();

        BigDecimal saldoRestante =
                valorDisponivel.subtract(gastoTotal);

        BigDecimal percentualGasto =
                BigDecimal.ZERO;


        if (renda.compareTo(BigDecimal.ZERO) > 0) {

            percentualGasto =
                    gastoTotal
                            .divide(
                                    renda,
                                    2,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)
                            );


        }

        return new PlanejamentoMensalResumoDTO(

                planejamento.getId(),
                planejamento.getReferencia(),
                renda,
                receitasMes,
                planejamento.getPercentualEconomia(),
                metaEconomia,
                valorDisponivel,
                gastoTotal,
                saldoRestante,
                percentualGasto
        );
    }


    public PlanejamentoDashboardDTO dashboard(Long planejamentoId) {

        PlanejamentoMensalResumoDTO resumoMensal =
                resumoMensal(planejamentoId);

        List<PlanejamentoCategoriaResumoDTO> categorias =
                planejamentoCategoriaService.resumo(planejamentoId);

        return new PlanejamentoDashboardDTO(
                resumoMensal,
                categorias
        );
    }


    private PlanejamentoResumoDTO toDTO(
            PlanejamentoMensal planejamento
    ) {

        return new PlanejamentoResumoDTO(
                planejamento.getId(),
                planejamento.getReferencia(),
                planejamento.getRendaMensal(),
                planejamento.getPercentualEconomia()
        );
    }
}