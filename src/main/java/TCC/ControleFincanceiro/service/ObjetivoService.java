package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.objetivo.ObjetivoAtualizarDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoCriarDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoResumoDTO;
import TCC.ControleFincanceiro.entity.*;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjetivoService {

    private final ObjetivoRepository objetivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjetivoDepositoRepository depositoRepository;

    private final TransacaoRepository transacaoRepository;



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
        objetivo.setImagem(dto.imagem());

        Objetivo salvo =
                objetivoRepository.save(objetivo);

        return toResumo(salvo);
    }


    public Page<ObjetivoResumoDTO> listar(
            Long usuarioId,
            Boolean finalizado,
            Pageable pageable
    ) {

        return objetivoRepository
                .findByUsuarioIdAndFinalizado(
                        usuarioId,
                        finalizado,
                        pageable
                )
                .map(this::toResumo);
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


        BigDecimal valorAtual =
                depositoRepository.calcularTotal(
                        objetivoId
                );

        if (valorAtual == null) {
            valorAtual = BigDecimal.ZERO;
        }


        if (
                dto.valorObjetivo() == null ||
                        dto.valorObjetivo()
                                .compareTo(
                                        BigDecimal.ZERO
                                ) <= 0
        ) {

            throw new RuntimeException(
                    "O valor da meta deve ser maior que zero"
            );
        }


        if (
                dto.valorObjetivo()
                        .compareTo(
                                valorAtual
                        ) < 0
        ) {

            throw new RuntimeException(
                    "A nova meta não pode ser menor que o valor já guardado no objetivo"
            );
        }

        objetivo.setNome(dto.nome());
        objetivo.setValorObjetivo(dto.valorObjetivo());
        objetivo.setDataFinal(dto.dataFinal());
        objetivo.setCor(dto.cor());
        objetivo.setIcone(dto.icone());
        objetivo.setImagem(dto.imagem());

        Objetivo atualizado =
                objetivoRepository.save(objetivo);

        return toResumo(atualizado);
    }



    @Transactional
    public void deletar(
            Long objetivoId,
            Long usuarioId
    ) {

        Objetivo objetivo =
                objetivoRepository.findById(objetivoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Objetivo não encontrado"
                                )
                        );


        if (
                !objetivo.getUsuario()
                        .getId()
                        .equals(usuarioId)
        ) {

            throw new RuntimeException(
                    "Acesso negado"
            );
        }


        /*
         * Objetivos concluídos fazem parte
         * do histórico e não podem ser excluídos.
         */
        if (
                Boolean.TRUE.equals(
                        objetivo.getFinalizado()
                )
        ) {

            throw new RuntimeException(
                    "Não é possível excluir um objetivo concluído"
            );
        }


        /*
         * Busca todas as movimentações
         * relacionadas ao objetivo.
         */
        List<ObjetivoDeposito> movimentacoes =
                depositoRepository.findByObjetivoId(
                        objetivoId
                );


        for (
                ObjetivoDeposito movimentacao :
                movimentacoes
        ) {

            /*
             * Cada movimentação possui uma
             * transação financeira vinculada.
             */
            Transacao transacao =
                    movimentacao.getTransacao();


            /*
             * Primeiro desvinculamos para evitar
             * problemas de chave estrangeira.
             */
            movimentacao.setTransacao(
                    null
            );

            depositoRepository.save(
                    movimentacao
            );


            /*
             * Remove a movimentação interna
             * do objetivo.
             */
            depositoRepository.delete(
                    movimentacao
            );


            /*
             * Remove também a transação.
             *
             * Como o saldo da conta é calculado
             * pelas transações, o dinheiro volta
             * automaticamente para o saldo.
             */
            if (
                    transacao != null
            ) {

                transacaoRepository.delete(
                        transacao
                );

            }

        }


        /*
         * Agora podemos apagar o objetivo.
         */
        objetivoRepository.delete(
                objetivo
        );
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
                objetivo.getFinalizado(),
                objetivo.getCor(),
                objetivo.getIcone(),
                objetivo.getImagem()
        );
    }




}