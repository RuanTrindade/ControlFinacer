package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoAtualizarDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCriarDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoMensalResumoDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoResumoDTO;
import TCC.ControleFincanceiro.entity.PlanejamentoCategoria;
import TCC.ControleFincanceiro.entity.PlanejamentoMensal;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.repository.PlanejamentoCategoriaRepository;
import TCC.ControleFincanceiro.repository.PlanejamentoMensalRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
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


    public PlanejamentoResumoDTO criar(
            PlanejamentoCriarDTO dto
    ) {

        if (planejamentoRepository
                .findByUsuarioIdAndReferencia(
                        dto.usuarioId(),
                        dto.referencia()
                )
                .isPresent()) {

            throw new RuntimeException(
                    "Já existe planejamento para esse mês"
            );
        }

        Usuario usuario =
                usuarioRepository.findById(dto.usuarioId())
                        .orElseThrow(() ->
                                new RuntimeException("Usuário não encontrado"));

        PlanejamentoMensal planejamento =
                new PlanejamentoMensal();

        planejamento.setUsuario(usuario);
        planejamento.setReferencia(dto.referencia());
        planejamento.setRendaMensal(dto.rendaMensal());
        planejamento.setPercentualEconomia(
                dto.percentualEconomia()
        );

        PlanejamentoMensal salvo =
                planejamentoRepository.save(planejamento);

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

        planejamento.setRendaMensal(dto.rendaMensal());
        planejamento.setPercentualEconomia(
                dto.percentualEconomia()
        );

        PlanejamentoMensal atualizado =
                planejamentoRepository.save(planejamento);

        return toDTO(atualizado);
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



    public PlanejamentoResumoDTO copiar(
            Long id,
            LocalDate novaReferencia
    ) {

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

        BigDecimal gastoTotal =
                transacaoRepository.totalDespesasPlanejadas(
                        usuarioId,
                        planejamentoId,
                        mes,
                        ano
                );

        if (gastoTotal == null) {
            gastoTotal = BigDecimal.ZERO;
        }

        BigDecimal renda =
                planejamento.getRendaMensal();

        BigDecimal economizado =
                renda.subtract(gastoTotal);

        BigDecimal percentualGasto =
                BigDecimal.ZERO;

        BigDecimal percentualEconomizado =
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

            percentualEconomizado =
                    economizado
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
                renda,
                gastoTotal,
                economizado,
                percentualGasto,
                percentualEconomizado
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