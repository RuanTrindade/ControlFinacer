package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.investimento.*;
import TCC.ControleFincanceiro.service.InvestimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investimentos")
@RequiredArgsConstructor
public class InvestimentoController {

    private final InvestimentoService investimentoService;

    @PostMapping
    public ResponseEntity<InvestimentoResumoDTO> criarInvestimento(
            @RequestBody InvestimentoCriarDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(investimentoService.criarInvestimento(dto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<InvestimentoResumoDTO> atualizarInvestimento(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestBody InvestimentoAtualizarDTO dto
    ) {
        return ResponseEntity.ok(
                investimentoService.atualizarInvestimento(id, usuarioId, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarInvestimento(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        investimentoService.deletarInvestimento(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<InvestimentoEstatisticaDTO> estatisticas(@PathVariable Long id) {
        return ResponseEntity.ok(investimentoService.obterEstatisticas(id));
    }


    @GetMapping("/{id}/resumo-mensal")
    public ResponseEntity<InvestimentoResumoMensalDTO>
    obterResumoMensal(
            @PathVariable Long id,
            @RequestParam Integer mes,
            @RequestParam Integer ano

    ) {

        return ResponseEntity.ok(
                investimentoService
                        .obterResumoMensal(
                                id,
                                mes,
                                ano
                        )

        );
    }


    @GetMapping("/usuario/{usuarioId}/resumo")
    public ResponseEntity<CarteiraInvestimentoResumoDTO>
    obterResumoCarteira(
            @PathVariable Long usuarioId,
            @RequestParam Integer mes,
            @RequestParam Integer ano

    ) {

        return ResponseEntity.ok(

                investimentoService
                        .obterResumoCarteira(
                                usuarioId,
                                mes,
                                ano
                        )

        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<InvestimentoCardDTO>> listarInvestimentos(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "false")
            Boolean encerrado,
            @RequestParam Integer mes,
            @RequestParam Integer ano,
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                investimentoService
                        .listarInvestimentos(
                                usuarioId,
                                encerrado,
                                mes,
                                ano,
                                pageable
                        )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<InvestimentoResumoDTO> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                investimentoService.buscarPorId(id)
        );
    }
}