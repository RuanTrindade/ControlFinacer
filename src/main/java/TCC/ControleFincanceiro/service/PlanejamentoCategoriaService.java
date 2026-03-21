package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResponseDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResumoDTO;
import TCC.ControleFincanceiro.entity.Categoria;
import TCC.ControleFincanceiro.entity.PlanejamentoCategoria;
import TCC.ControleFincanceiro.entity.PlanejamentoMensal;
import TCC.ControleFincanceiro.repository.CategoriaRepository;
import TCC.ControleFincanceiro.repository.PlanejamentoCategoriaRepository;
import TCC.ControleFincanceiro.repository.PlanejamentoMensalRepository;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanejamentoCategoriaService {

    private final PlanejamentoCategoriaRepository categoriaRepository;
    private final PlanejamentoMensalRepository planejamentoRepository;
    private final CategoriaRepository categoriaBaseRepository;
    private final TransacaoRepository transacaoRepository;

    // ==============================
    // SALVAR
    // ==============================
    public PlanejamentoCategoriaResponseDTO salvar(PlanejamentoCategoriaDTO dto) {

        PlanejamentoMensal planejamento = planejamentoRepository.findById(dto.planejamentoId())
                .orElseThrow(() -> new RuntimeException("Planejamento não encontrado"));

        Categoria categoria = categoriaBaseRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (!categoria.getTipo().name().equals("DESPESA")) {
            throw new RuntimeException("Somente categorias de despesa podem ser planejadas");
        }

        BigDecimal somaAtual = categoriaRepository.somaLimites(dto.planejamentoId());
        if (somaAtual == null) somaAtual = BigDecimal.ZERO;

        if (somaAtual.add(dto.limite()).compareTo(planejamento.getRendaMensal()) > 0) {
            throw new RuntimeException("Soma das categorias ultrapassa a renda mensal");
        }

        PlanejamentoCategoria pc = new PlanejamentoCategoria();
        pc.setPlanejamentoMensal(planejamento);
        pc.setCategoria(categoria);
        pc.setLimite(dto.limite());

        PlanejamentoCategoria salvo = categoriaRepository.save(pc);

        return new PlanejamentoCategoriaResponseDTO(
                salvo.getId(),
                salvo.getCategoria().getNome(),
                salvo.getLimite()
        );
    }

    // ==============================
    // LISTAR
    // ==============================
    public List<PlanejamentoCategoriaResponseDTO> listar(Long planejamentoId) {

        return categoriaRepository.findByPlanejamentoMensalId(planejamentoId)
                .stream()
                .map(pc -> new PlanejamentoCategoriaResponseDTO(
                        pc.getId(),
                        pc.getCategoria().getNome(),
                        pc.getLimite()
                ))
                .toList();
    }

    // ==============================
    // DELETAR
    // ==============================
    public void deletar(Long id) {
        categoriaRepository.deleteById(id);
    }

    // ==============================
    // RESUMO (TOP 🔥)
    // ==============================
    public List<PlanejamentoCategoriaResumoDTO> resumo(Long planejamentoId) {

        PlanejamentoMensal planejamento = planejamentoRepository.findById(planejamentoId)
                .orElseThrow(() -> new RuntimeException("Planejamento não encontrado"));

        int mes = planejamento.getReferencia().getMonthValue();
        int ano = planejamento.getReferencia().getYear();
        Long usuarioId = planejamento.getUsuario().getId();

        return categoriaRepository.findByPlanejamentoMensalId(planejamentoId)
                .stream()
                .map(pc -> {

                    BigDecimal gasto = transacaoRepository.totalPorCategoriaNoMes(
                            usuarioId,
                            pc.getCategoria().getId(),
                            mes,
                            ano
                    );

                    if (gasto == null) gasto = BigDecimal.ZERO;

                    BigDecimal planejado = pc.getLimite();

                    String status;

                    if (gasto.compareTo(planejado) > 0) {
                        status = "ESTOUROU";
                    } else if (gasto.compareTo(planejado.multiply(BigDecimal.valueOf(0.8))) >= 0) {
                        status = "ATENÇÃO";
                    } else {
                        status = "OK";
                    }

                    return new PlanejamentoCategoriaResumoDTO(
                            pc.getCategoria().getNome(),
                            planejado,
                            gasto,
                            status
                    );
                })
                .toList();
    }


    public List<PlanejamentoCategoriaResumoDTO> resumoPorUsuario(Long usuarioId) {

        List<PlanejamentoMensal> planejamentos =
                planejamentoRepository.findByUsuarioIdOrderByReferenciaDesc(usuarioId);

        if (planejamentos.isEmpty()) {
            return List.of();
        }

        // pega o planejamento mais recente
        PlanejamentoMensal planejamento = planejamentos.get(0);

        return resumo(planejamento.getId());
    }
}