package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.investimento.InvestimentoMovimentacaoDTO;
import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;
import TCC.ControleFincanceiro.service.InvestimentoMovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/investimentos")
@RequiredArgsConstructor
public class InvestimentoMovimentacaoController {

    private final InvestimentoMovimentacaoService movimentacaoService;

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<InvestimentoMovimentacaoDTO> registrarMovimentacao(
            @PathVariable Long id,
            @RequestParam BigDecimal valor,
            @RequestParam TipoInvestimento tipo
    ) {
        return ResponseEntity.ok(
                movimentacaoService.registrarMovimentacao(id, valor, tipo)
        );
    }

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<List<InvestimentoMovimentacaoDTO>> listarMovimentacoes(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                movimentacaoService.listarMovimentacoes(id)
        );
    }
}