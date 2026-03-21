package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.objetivo.ObjetivoAtualizarDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoCriarDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoResumoDTO;
import TCC.ControleFincanceiro.entity.Objetivo;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.repository.ObjetivoDepositoRepository;
import TCC.ControleFincanceiro.repository.ObjetivoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjetivoService {

    private final ObjetivoRepository objetivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjetivoDepositoRepository depositoRepository;


    public ObjetivoResumoDTO criar(ObjetivoCriarDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Objetivo obj = new Objetivo();
        obj.setNome(dto.nome());
        obj.setValorObjetivo(dto.valorObjetivo());
        obj.setDataFinal(dto.dataFinal());
        obj.setCor(dto.cor());
        obj.setIcone(dto.icone());
        obj.setUsuario(usuario);
        obj.setFinalizado(false);

        objetivoRepository.save(obj);

        return toResumo(obj);
    }


    public List<ObjetivoResumoDTO> listar(Long usuarioId) {

        return objetivoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResumo)
                .toList();
    }


    public ObjetivoResumoDTO atualizar(Long objetivoId, Long usuarioId, ObjetivoAtualizarDTO dto) {

        Objetivo obj = objetivoRepository.findById(objetivoId)
                .orElseThrow(() -> new RuntimeException("Objetivo não encontrado"));

        if (!obj.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado");
        }

        if (obj.getFinalizado()) {
            throw new RuntimeException("Objetivo já finalizado");
        }

        obj.setNome(dto.nome());
        obj.setValorObjetivo(dto.valorObjetivo());
        obj.setDataFinal(dto.dataFinal());
        obj.setCor(dto.cor());
        obj.setIcone(dto.icone());

        objetivoRepository.save(obj);

        return toResumo(obj);
    }


    public void deletar(Long objetivoId, Long usuarioId) {

        Objetivo obj = objetivoRepository.findById(objetivoId)
                .orElseThrow(() -> new RuntimeException("Objetivo não encontrado"));

        if (!obj.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado");
        }

        BigDecimal saldo = depositoRepository.calcularTotal(objetivoId);

        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Não é possível excluir objetivo com saldo");
        }

        objetivoRepository.delete(obj);
    }


    private ObjetivoResumoDTO toResumo(Objetivo obj) {

        BigDecimal total = depositoRepository.calcularTotal(obj.getId());

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        // FINALIZA AUTOMATICAMENTE
        if (!obj.getFinalizado()
                && obj.getValorObjetivo() != null
                && total.compareTo(obj.getValorObjetivo()) >= 0) {

            obj.setFinalizado(true);
            objetivoRepository.save(obj);
        }

        BigDecimal percentual = BigDecimal.ZERO;

        if (obj.getValorObjetivo() != null
                && obj.getValorObjetivo().compareTo(BigDecimal.ZERO) > 0) {

            percentual = total
                    .divide(obj.getValorObjetivo(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return new ObjetivoResumoDTO(
                obj.getId(),
                obj.getNome(),
                obj.getValorObjetivo(),
                total,
                percentual,
                obj.getDataFinal(),
                obj.getFinalizado()
        );
    }
}