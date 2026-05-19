package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.investimento.InvestimentoAtualizarDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoCriarDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoEstatisticaDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoResumoDTO;
import TCC.ControleFincanceiro.entity.Investimento;
import TCC.ControleFincanceiro.entity.Usuario;
import TCC.ControleFincanceiro.entity.enumerated.TipoAtivo;
import TCC.ControleFincanceiro.repository.InvestimentoMovimentacaoRepository;
import TCC.ControleFincanceiro.repository.InvestimentoRepository;
import TCC.ControleFincanceiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestimentoService {

    private final InvestimentoRepository investimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InvestimentoMovimentacaoRepository movimentacaoRepository;



    public InvestimentoResumoDTO criarInvestimento(
            InvestimentoCriarDTO dto
    ) {

        Usuario usuario =
                usuarioRepository.findById(dto.usuarioId())
                        .orElseThrow(() ->
                                new RuntimeException("Usuário não encontrado"));

        Investimento investimento =
                new Investimento();

        investimento.setNome(dto.nome());
        investimento.setTipo(
                TipoAtivo.valueOf(dto.tipo())
        );
        investimento.setUsuario(usuario);

        Investimento salvo =
                investimentoRepository.save(investimento);

        return toResumoDTO(salvo);
    }


    public List<InvestimentoResumoDTO> listarInvestimentos(
            Long usuarioId
    ) {

        return investimentoRepository
                .findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResumoDTO)
                .toList();
    }



    public InvestimentoResumoDTO buscarPorId(
            Long investimentoId
    ) {

        Investimento investimento =
                investimentoRepository.findById(investimentoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Investimento não encontrado"
                                ));

        return toResumoDTO(investimento);
    }



    public InvestimentoResumoDTO atualizarInvestimento(
            Long investimentoId,
            Long usuarioId,
            InvestimentoAtualizarDTO dto
    ) {

        Investimento investimento =
                investimentoRepository.findById(investimentoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Investimento não encontrado"
                                ));



        if (!investimento.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new RuntimeException("Acesso negado");
        }

        investimento.setNome(dto.nome());

        investimento.setTipo(
                TipoAtivo.valueOf(dto.tipo())
        );

        investimento.setTaxaAtual(dto.taxaAtual());

        Investimento salvo =
                investimentoRepository.save(investimento);

        return toResumoDTO(salvo);
    }



    public void deletarInvestimento(
            Long investimentoId,
            Long usuarioId
    ) {

        Investimento investimento =
                investimentoRepository.findById(investimentoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Investimento não encontrado"
                                ));



        if (!investimento.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new RuntimeException("Acesso negado");
        }

        investimentoRepository.delete(investimento);
    }



    public InvestimentoEstatisticaDTO obterEstatisticas(
            Long investimentoId
    ) {

        Investimento investimento =
                investimentoRepository.findById(investimentoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Investimento não encontrado"
                                ));

        BigDecimal aportes =
                movimentacaoRepository.totalAportes(investimentoId);

        BigDecimal resgates =
                movimentacaoRepository.totalResgates(investimentoId);

        BigDecimal rendimento =
                movimentacaoRepository.totalRendimento(investimentoId);

        BigDecimal saldo =
                movimentacaoRepository.calcularSaldo(investimentoId);



        if (aportes == null) {
            aportes = BigDecimal.ZERO;
        }

        if (resgates == null) {
            resgates = BigDecimal.ZERO;
        }

        if (rendimento == null) {
            rendimento = BigDecimal.ZERO;
        }

        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }

        return new InvestimentoEstatisticaDTO(
                investimento.getId(),
                investimento.getNome(),
                aportes,
                resgates,
                rendimento,
                saldo
        );
    }



    private InvestimentoResumoDTO toResumoDTO(
            Investimento investimento
    ) {

        return new InvestimentoResumoDTO(
                investimento.getId(),
                investimento.getNome(),
                investimento.getTipo(),
                investimento.getTaxaAtual()
        );
    }
}