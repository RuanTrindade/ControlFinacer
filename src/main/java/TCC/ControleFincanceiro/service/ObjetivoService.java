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



    public ObjetivoResumoDTO criar(
            ObjetivoCriarDTO dto
    ) {

        Usuario usuario =
                usuarioRepository.findById(dto.usuarioId())
                        .orElseThrow(() ->
                                new RuntimeException("Usuário não encontrado"));

        Objetivo objetivo = new Objetivo();

        objetivo.setNome(dto.nome());
        objetivo.setValorObjetivo(dto.valorObjetivo());
        objetivo.setDataFinal(dto.dataFinal());
        objetivo.setCor(dto.cor());
        objetivo.setIcone(dto.icone());
        objetivo.setUsuario(usuario);
        objetivo.setFinalizado(false);

        Objetivo salvo =
                objetivoRepository.save(objetivo);

        return toResumo(salvo);
    }



    public List<ObjetivoResumoDTO> listar(
            Long usuarioId
    ) {

        return objetivoRepository
                .findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResumo)
                .toList();
    }


    public ObjetivoResumoDTO buscarPorId(
            Long objetivoId
    ) {

        Objetivo objetivo =
                objetivoRepository.findById(objetivoId)
                        .orElseThrow(() ->
                                new RuntimeException("Objetivo não encontrado"));

        return toResumo(objetivo);
    }



    public ObjetivoResumoDTO atualizar(
            Long objetivoId,
            Long usuarioId,
            ObjetivoAtualizarDTO dto
    ) {

        Objetivo objetivo =
                objetivoRepository.findById(objetivoId)
                        .orElseThrow(() ->
                                new RuntimeException("Objetivo não encontrado"));

        if (!objetivo.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new RuntimeException("Acesso negado");
        }

        if (objetivo.getFinalizado()) {
            throw new RuntimeException("Objetivo já finalizado");
        }

        objetivo.setNome(dto.nome());
        objetivo.setValorObjetivo(dto.valorObjetivo());
        objetivo.setDataFinal(dto.dataFinal());
        objetivo.setCor(dto.cor());
        objetivo.setIcone(dto.icone());

        Objetivo atualizado =
                objetivoRepository.save(objetivo);

        return toResumo(atualizado);
    }



    public void deletar(
            Long objetivoId,
            Long usuarioId
    ) {

        Objetivo objetivo =
                objetivoRepository.findById(objetivoId)
                        .orElseThrow(() ->
                                new RuntimeException("Objetivo não encontrado"));

        if (!objetivo.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new RuntimeException("Acesso negado");
        }

        BigDecimal saldo =
                depositoRepository.calcularTotal(objetivoId);

        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException(
                    "Não é possível excluir objetivo com saldo"
            );
        }

        objetivoRepository.delete(objetivo);
    }



    private ObjetivoResumoDTO toResumo(
            Objetivo objetivo
    ) {

        BigDecimal total =
                depositoRepository.calcularTotal(
                        objetivo.getId()
                );

        if (total == null) {
            total = BigDecimal.ZERO;
        }


        if (!objetivo.getFinalizado()
                && objetivo.getValorObjetivo() != null
                && total.compareTo(
                objetivo.getValorObjetivo()
        ) >= 0) {

            objetivo.setFinalizado(true);

            objetivoRepository.save(objetivo);
        }



        BigDecimal percentual =
                BigDecimal.ZERO;

        if (objetivo.getValorObjetivo() != null
                && objetivo.getValorObjetivo()
                .compareTo(BigDecimal.ZERO) > 0) {

            percentual =
                    total
                            .divide(
                                    objetivo.getValorObjetivo(),
                                    2,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)
                            );
        }

        return new ObjetivoResumoDTO(
                objetivo.getId(),
                objetivo.getNome(),
                objetivo.getValorObjetivo(),
                total,
                percentual,
                objetivo.getDataFinal(),
                objetivo.getFinalizado()
        );
    }
}