package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.objetivo.ObjetivoMovimentacaoDTO;
import TCC.ControleFincanceiro.service.ObjetivoDepositoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/objetivos")
@RequiredArgsConstructor
public class ObjetivoMovimentacaoController {

    private final ObjetivoDepositoService depositoService;

    @PostMapping("/{id}/movimentar")
    public ResponseEntity<ObjetivoMovimentacaoDTO> movimentar(
            @PathVariable Long id,
            @RequestParam BigDecimal valor,
            @RequestParam String tipo
    ) {
        return ResponseEntity.ok(
                depositoService.movimentar(id, valor, tipo)
        );
    }

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<List<ObjetivoMovimentacaoDTO>> listar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                depositoService.listar(id)
        );
    }
}