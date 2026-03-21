package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.investimento.InvestimentoAtualizarDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoCriarDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoEstatisticaDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoResumoDTO;
import TCC.ControleFincanceiro.service.InvestimentoService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<InvestimentoResumoDTO>> listarInvestimentos(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(investimentoService.listarInvestimentos(usuarioId));
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
}