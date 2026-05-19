package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.investimento.InvestimentoMovimentacaoDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoMovimentacaoRequestDTO;
import TCC.ControleFincanceiro.service.InvestimentoMovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investimentos")
@RequiredArgsConstructor
public class InvestimentoMovimentacaoController {

    private final InvestimentoMovimentacaoService movimentacaoService;


    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<InvestimentoMovimentacaoDTO> registrarMovimentacao(
            @PathVariable Long id,
            @RequestBody InvestimentoMovimentacaoRequestDTO dto
    ) {

        return ResponseEntity.ok(
                movimentacaoService.registrarMovimentacao(
                        id,
                        dto.valor(),
                        dto.tipo()
                )
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


    @PutMapping("/movimentacoes/{movimentacaoId}")
    public ResponseEntity<InvestimentoMovimentacaoDTO> editarMovimentacao(
            @PathVariable Long movimentacaoId,
            @RequestBody InvestimentoMovimentacaoRequestDTO dto
    ) {

        return ResponseEntity.ok(
                movimentacaoService.editarMovimentacao(
                        movimentacaoId,
                        dto.valor()
                )
        );
    }


    @DeleteMapping("/movimentacoes/{movimentacaoId}")
    public ResponseEntity<Void> deletarMovimentacao(
            @PathVariable Long movimentacaoId
    ) {

        movimentacaoService.deletarMovimentacao(movimentacaoId);

        return ResponseEntity.noContent().build();
    }
}