package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResponseDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResumoDTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.PlanejamentoCategoria;
import TCC.ControleFincanceiro.entity.PlanejamentoMensal;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.PlanejamentoCategoriaRepository;
import TCC.ControleFincanceiro.repository.PlanejamentoMensalRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanejamentoCategoriaService {

    private final PlanejamentoCategoriaRepository categoriaRepository;
    private final PlanejamentoMensalRepository planejamentoRepository;
    private final CategoriaRepository categoriaBaseRepository;
    private final TransacaoRepository transacaoRepository;


    public PlanejamentoCategoriaResponseDTO salvar(
            PlanejamentoCategoriaDTO dto
    ) {

        if (
                dto.limite() == null ||
                        dto.limite()
                                .compareTo(BigDecimal.ZERO) <= 0
        ) {
            throw new RuntimeException(
                    "O limite deve ser maior que zero"
            );
        }

        PlanejamentoMensal planejamento =
                planejamentoRepository.findById(dto.planejamentoId())
                        .orElseThrow(() ->
                                new RuntimeException("Planejamento não encontrado"));

        Categoria categoria =
                categoriaBaseRepository.findById(dto.categoriaId())
                        .orElseThrow(() ->
                                new RuntimeException("Categoria não encontrada"));

        boolean categoriaPadrao =
                Boolean.TRUE.equals(
                        categoria.getPadraoSistema()
                );

        boolean categoriaDoUsuario =
                categoria.getUsuario() != null &&
                        categoria.getUsuario()
                                .getId()
                                .equals(
                                        planejamento.getUsuario().getId()
                                );

        if (!categoriaPadrao && !categoriaDoUsuario) {
            throw new RuntimeException(
                    "A categoria não pertence ao usuário deste planejamento"
            );
        }

        if (
                categoriaRepository
                        .existsByPlanejamentoMensalIdAndCategoriaId(
                                dto.planejamentoId(),
                                dto.categoriaId()
                        )
        ) {
            throw new RuntimeException(
                    "Essa categoria já foi adicionada ao planejamento."
            );
        }

        if (!categoria.getTipo().name().equals("DESPESA")) {
            throw new RuntimeException(
                    "Somente categorias de despesa podem ser planejadas"
            );
        }

        PlanejamentoCategoria pc = new PlanejamentoCategoria();

        pc.setPlanejamentoMensal(planejamento);
        pc.setCategoria(categoria);
        pc.setLimite(dto.limite());
        PlanejamentoCategoria salvo =
                categoriaRepository.save(pc);


        /*
         * Depois de adicionar a categoria,
         * verifica quanto todas as categorias
         * planejadas somam.
         */
        BigDecimal somaLimites =
                categoriaRepository.somaLimites(
                        planejamento.getId()
                );

        if (somaLimites == null) {
            somaLimites = BigDecimal.ZERO;
        }


        /*
         * Se a soma das categorias passou do
         * valor atual do planejamento,
         * aumentamos o valorPlanejado.
         *
         * Nunca diminuímos aqui.
         */
        if (
                somaLimites.compareTo(
                        planejamento.getValorPlanejado()
                ) > 0
        ) {

            planejamento.setValorPlanejado(
                    somaLimites
            );

            planejamentoRepository.save(
                    planejamento
            );
        }


        return new PlanejamentoCategoriaResponseDTO(
                salvo.getId(),
                salvo.getCategoria().getNome(),
                salvo.getLimite()
        );
    }


    public List<PlanejamentoCategoriaResponseDTO> listar(
            Long planejamentoId
    ) {

        return categoriaRepository
                .findByPlanejamentoMensalId(planejamentoId)
                .stream()
                .map(pc -> new PlanejamentoCategoriaResponseDTO(
                        pc.getId(),
                        pc.getCategoria().getNome(),
                        pc.getLimite()
                ))
                .toList();
    }



    public PlanejamentoCategoriaResponseDTO editar(
            Long id,
            Long usuarioId,
            BigDecimal novoLimite
    ) {

        PlanejamentoCategoria pc =
                categoriaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria planejada não encontrada"
                                ));

        if (
                !pc.getPlanejamentoMensal()
                        .getUsuario()
                        .getId()
                        .equals(usuarioId)
        ) {
            throw new RuntimeException(
                    "Acesso negado"
            );
        }

        if (novoLimite == null ||
                novoLimite.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException("O limite deve ser maior que zero");
        }


        pc.setLimite(
                novoLimite
        );

        categoriaRepository.save(
                pc
        );


        /*
         * Pegamos o planejamento ao qual
         * essa categoria pertence.
         */
        PlanejamentoMensal planejamento =
                pc.getPlanejamentoMensal();


        /*
         * Recalcula a soma de TODAS
         * as categorias planejadas.
         */
        BigDecimal somaLimites =
                categoriaRepository.somaLimites(
                        planejamento.getId()
                );

        if (somaLimites == null) {
            somaLimites = BigDecimal.ZERO;
        }


        /*
         * Só aumenta o valorPlanejado.
         *
         * Se a categoria for reduzida depois,
         * o planejamento NÃO volta ao valor antigo.
         */
        if (
                somaLimites.compareTo(
                        planejamento.getValorPlanejado()
                ) > 0
        ) {

            planejamento.setValorPlanejado(
                    somaLimites
            );

            planejamentoRepository.save(
                    planejamento
            );
        }


        return new PlanejamentoCategoriaResponseDTO(
                pc.getId(),
                pc.getCategoria().getNome(),
                pc.getLimite()
        );
    }



    public void deletar(
            Long id,
            Long usuarioId
    ) {

        PlanejamentoCategoria pc =
                categoriaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria planejada não encontrada"
                                ));

        if (
                !pc.getPlanejamentoMensal()
                        .getUsuario()
                        .getId()
                        .equals(usuarioId)
        ) {
            throw new RuntimeException(
                    "Acesso negado"
            );
        }

        categoriaRepository.delete(pc);
    }


    private BigDecimal calcularValorDisponivel(
            PlanejamentoMensal planejamento
    ) {

        return planejamento
                .getValorPlanejado();
    }


    public List<PlanejamentoCategoriaResumoDTO> resumo(
            Long planejamentoId
    ) {

        PlanejamentoMensal planejamento =
                planejamentoRepository.findById(planejamentoId)
                        .orElseThrow(() ->
                                new RuntimeException("Planejamento não encontrado"));

        int mes = planejamento.getReferencia().getMonthValue();
        int ano = planejamento.getReferencia().getYear();
        Long usuarioId = planejamento.getUsuario().getId();

        BigDecimal valorDisponivel =
                calcularValorDisponivel(
                        planejamento
                );

        BigDecimal somaLimites =
                categoriaRepository.somaLimites(
                        planejamentoId
                );

        if (somaLimites == null) {
            somaLimites = BigDecimal.ZERO;
        }

        BigDecimal limiteCategoriasRestantes =
                valorDisponivel
                        .subtract(somaLimites)
                        .max(BigDecimal.ZERO);


        List<PlanejamentoCategoriaResumoDTO> resultado =
                new java.util.ArrayList<>(
                        categoriaRepository
                                .findByPlanejamentoMensalId(planejamentoId)
                                .stream()
                                .map(pc -> {

                                    BigDecimal totalPago =
                                            transacaoRepository
                                                    .totalPorCategoriaNoMesEStatus(
                                                            usuarioId,
                                                            pc.getCategoria().getId(),
                                                            StatusPagamento.PAGO,
                                                            mes,
                                                            ano
                                                    );

                                    BigDecimal totalPendente =
                                            transacaoRepository
                                                    .totalPorCategoriaNoMesEStatus(
                                                            usuarioId,
                                                            pc.getCategoria().getId(),
                                                            StatusPagamento.PENDENTE,
                                                            mes,
                                                            ano
                                                    );

                                    if (totalPago == null) {
                                        totalPago = BigDecimal.ZERO;
                                    }

                                    if (totalPendente == null) {
                                        totalPendente = BigDecimal.ZERO;
                                    }

                                    BigDecimal limite =
                                            pc.getLimite();


                                    BigDecimal pago =
                                            totalPago.min(limite);


                                    BigDecimal pagoUltrapassado =
                                            totalPago
                                                    .subtract(limite)
                                                    .max(BigDecimal.ZERO);


                                    BigDecimal disponivelDepoisDoPago =
                                            limite
                                                    .subtract(pago)
                                                    .max(BigDecimal.ZERO);


                                    BigDecimal pendente =
                                            totalPendente.min(
                                                    disponivelDepoisDoPago
                                            );


                                    BigDecimal pendenteUltrapassado =
                                            totalPendente
                                                    .subtract(
                                                            disponivelDepoisDoPago
                                                    )
                                                    .max(BigDecimal.ZERO);


                                    BigDecimal totalComprometido =
                                            totalPago.add(totalPendente);


                                    BigDecimal restante =
                                            limite
                                                    .subtract(totalComprometido)
                                                    .max(BigDecimal.ZERO);


                                    BigDecimal percentualUtilizado =
                                            BigDecimal.ZERO;

                                    if (
                                            limite.compareTo(
                                                    BigDecimal.ZERO
                                            ) > 0
                                    ) {

                                        percentualUtilizado =
                                                totalComprometido
                                                        .divide(
                                                                limite,
                                                                4,
                                                                RoundingMode.HALF_UP
                                                        )
                                                        .multiply(
                                                                BigDecimal.valueOf(100)
                                                        )
                                                        .setScale(
                                                                2,
                                                                RoundingMode.HALF_UP
                                                        );
                                    }


                                    String status;

                                    if (
                                            totalComprometido
                                                    .compareTo(limite) > 0
                                    ) {

                                        status = "ESTOUROU";

                                    } else if (
                                            totalComprometido
                                                    .compareTo(
                                                            limite.multiply(
                                                                    BigDecimal.valueOf(0.8)
                                                            )
                                                    ) >= 0
                                    ) {

                                        status = "ATENÇÃO";

                                    } else {

                                        status = "OK";
                                    }


                                    return new PlanejamentoCategoriaResumoDTO(
                                            pc.getId(),
                                            pc.getCategoria().getId(),
                                            pc.getCategoria().getNome(),
                                            pc.getCategoria().getIcone(),
                                            pc.getCategoria().getCor(),
                                            limite,
                                            pago,
                                            pendente,
                                            pagoUltrapassado,
                                            pendenteUltrapassado,
                                            restante,
                                            percentualUtilizado,
                                            status
                                    );
                                })
                                .toList()
                );


        /*
         * =========================================
         * CATEGORIAS RESTANTES
         * =========================================
         */

        BigDecimal totalPagoNaoPlanejado =
                transacaoRepository
                        .totalDespesasNaoPlanejadasPorStatus(
                                usuarioId,
                                planejamentoId,
                                StatusPagamento.PAGO,
                                mes,
                                ano
                        );

        BigDecimal totalPendenteNaoPlanejado =
                transacaoRepository
                        .totalDespesasNaoPlanejadasPorStatus(
                                usuarioId,
                                planejamentoId,
                                StatusPagamento.PENDENTE,
                                mes,
                                ano
                        );


        if (totalPagoNaoPlanejado == null) {
            totalPagoNaoPlanejado =
                    BigDecimal.ZERO;
        }

        if (totalPendenteNaoPlanejado == null) {
            totalPendenteNaoPlanejado =
                    BigDecimal.ZERO;
        }


        BigDecimal totalNaoPlanejado =
                totalPagoNaoPlanejado
                        .add(
                                totalPendenteNaoPlanejado
                        );


        /*
         * Parte paga que cabe dentro do saldo
         * ainda não categorizado.
         */
        BigDecimal pagoRestante =
                totalPagoNaoPlanejado
                        .min(
                                limiteCategoriasRestantes
                        );


        /*
         * Parte paga que ultrapassou esse saldo.
         */
        BigDecimal pagoUltrapassadoRestante =
                totalPagoNaoPlanejado
                        .subtract(
                                limiteCategoriasRestantes
                        )
                        .max(
                                BigDecimal.ZERO
                        );


        /*
         * Quanto ainda sobra depois das despesas pagas.
         */
        BigDecimal disponivelDepoisDoPagoRestante =
                limiteCategoriasRestantes
                        .subtract(
                                pagoRestante
                        )
                        .max(
                                BigDecimal.ZERO
                        );


        /*
         * Parte pendente que ainda cabe.
         */
        BigDecimal pendenteRestante =
                totalPendenteNaoPlanejado
                        .min(
                                disponivelDepoisDoPagoRestante
                        );


        /*
         * Parte pendente que ultrapassa.
         */
        BigDecimal pendenteUltrapassadoRestante =
                totalPendenteNaoPlanejado
                        .subtract(
                                disponivelDepoisDoPagoRestante
                        )
                        .max(
                                BigDecimal.ZERO
                        );


        BigDecimal restanteCategorias =
                limiteCategoriasRestantes
                        .subtract(
                                totalNaoPlanejado
                        )
                        .max(
                                BigDecimal.ZERO
                        );


        BigDecimal percentualCategoriasRestantes =
                BigDecimal.ZERO;

        if (
                limiteCategoriasRestantes
                        .compareTo(BigDecimal.ZERO) > 0
        ) {

            percentualCategoriasRestantes =
                    totalNaoPlanejado
                            .divide(
                                    limiteCategoriasRestantes,
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }


        String statusCategoriasRestantes;

        if (
                totalNaoPlanejado
                        .compareTo(
                                limiteCategoriasRestantes
                        ) > 0
        ) {

            statusCategoriasRestantes =
                    "ESTOUROU";

        } else if (
                limiteCategoriasRestantes
                        .compareTo(BigDecimal.ZERO) > 0
                        &&
                        totalNaoPlanejado
                                .compareTo(
                                        limiteCategoriasRestantes
                                                .multiply(
                                                        BigDecimal.valueOf(0.8)
                                                )
                                ) >= 0
        ) {

            statusCategoriasRestantes =
                    "ATENÇÃO";

        } else {

            statusCategoriasRestantes =
                    "OK";
        }


        /*
         * Só adiciona a linha se:
         *
         * 1 - ainda existe dinheiro não categorizado
         *
         * OU
         *
         * 2 - existem despesas feitas em categorias
         *     que não pertencem ao planejamento.
         */
        if (
                limiteCategoriasRestantes
                        .compareTo(BigDecimal.ZERO) > 0
                        ||
                        totalNaoPlanejado
                                .compareTo(BigDecimal.ZERO) > 0
        ) {

            PlanejamentoCategoriaResumoDTO categoriasRestantes =
                    new PlanejamentoCategoriaResumoDTO(
                            null,
                            null,
                            "Categorias restantes",
                            null,
                            "#9ca3af",
                            limiteCategoriasRestantes,
                            pagoRestante,
                            pendenteRestante,
                            pagoUltrapassadoRestante,
                            pendenteUltrapassadoRestante,
                            restanteCategorias,
                            percentualCategoriasRestantes,
                            statusCategoriasRestantes
                    );

            resultado.add(
                    categoriasRestantes
            );
        }


        return resultado;
    }


    public Page<PlanejamentoCategoriaResumoDTO> resumoPaginado(
            Long planejamentoId,
            int pagina
    ) {

        /*
         * Quantidade fixa por página.
         */
        int tamanhoPagina = 4;


        /*
         * Reaproveitamos TODO o resumo existente.
         *
         * Isso é importante porque aqui já entram:
         *
         * - categorias normais
         * - pagos
         * - pendentes
         * - ultrapassados
         * - Categorias restantes
         */
        List<PlanejamentoCategoriaResumoDTO> lista =
                resumo(
                        planejamentoId
                );


        /*
         * Evita página negativa.
         */
        if (pagina < 0) {
            pagina = 0;
        }


        int inicio =
                pagina *
                        tamanhoPagina;


        int fim =
                Math.min(
                        inicio +
                                tamanhoPagina,
                        lista.size()
                );


        List<PlanejamentoCategoriaResumoDTO> conteudo;


        if (
                inicio >= lista.size()
        ) {

            conteudo =
                    List.of();

        } else {

            conteudo =
                    lista.subList(
                            inicio,
                            fim
                    );

        }


        return new PageImpl<>(
                conteudo,

                PageRequest.of(
                        pagina,
                        tamanhoPagina
                ),

                lista.size()
        );
    }

    // =========================================
    // RESUMO POR USUÁRIO
    // =========================================

    public List<PlanejamentoCategoriaResumoDTO> resumoPorUsuario(
            Long usuarioId
    ) {

        List<PlanejamentoMensal> planejamentos =
                planejamentoRepository
                        .findByUsuarioIdOrderByReferenciaDesc(usuarioId);

        if (planejamentos.isEmpty()) {
            return List.of();
        }

        PlanejamentoMensal planejamento =
                planejamentos.get(0);

        return resumo(planejamento.getId());
    }
}