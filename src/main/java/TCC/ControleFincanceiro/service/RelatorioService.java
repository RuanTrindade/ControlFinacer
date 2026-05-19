package TCC.ControleFincanceiro.service;

import TCC.ControleFincanceiro.dto.relatorio.RelatorioDTO;
import TCC.ControleFincanceiro.dto.relatorio.RelatorioMensalDTO;
import TCC.ControleFincanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final TransacaoRepository transacaoRepository;


    public RelatorioDTO gerarRelatorio(
            Long usuarioId
    ) {

        List<Object[]> dados =
                transacaoRepository.relatorioMensal(usuarioId);

        Map<String, RelatorioMensalDTO> mapa =
                new LinkedHashMap<>();



        for (Object[] row : dados) {

            int mes = (int) row[0];
            int ano = (int) row[1];

            String tipo =
                    row[2].toString();

            BigDecimal valor =
                    (BigDecimal) row[3];

            String chave =
                    String.format("%02d/%d", mes, ano);



            mapa.putIfAbsent(
                    chave,
                    new RelatorioMensalDTO(
                            chave,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    )
            );

            RelatorioMensalDTO atual =
                    mapa.get(chave);

            BigDecimal receita =
                    atual.receita();

            BigDecimal despesa =
                    atual.despesa();

            BigDecimal investimento =
                    atual.investimento();



            switch (tipo) {

                case "RECEITA" ->
                        receita = receita.add(valor);

                case "DESPESA" ->
                        despesa = despesa.add(valor);

                case "INVESTIMENTO" ->
                        investimento = investimento.add(valor);
            }

            mapa.put(
                    chave,
                    new RelatorioMensalDTO(
                            chave,
                            receita,
                            despesa,
                            investimento
                    )
            );
        }



        List<RelatorioMensalDTO> historico =
                new ArrayList<>(mapa.values());



        BigDecimal receitaTotal =
                historico.stream()
                        .map(RelatorioMensalDTO::receita)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );



        BigDecimal despesaTotal =
                historico.stream()
                        .map(RelatorioMensalDTO::despesa)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal investimentoTotal =
                historico.stream()
                        .map(RelatorioMensalDTO::investimento)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );



        return new RelatorioDTO(
                historico,
                receitaTotal,
                despesaTotal,
                investimentoTotal
        );
    }
}