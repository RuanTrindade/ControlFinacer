package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.objetivo.ObjetivoMovimentacaoDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoMovimentacaoRequestDTO;
import TCC.ControleFincanceiro.service.ObjetivoDepositoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/objetivos")
@RequiredArgsConstructor
public class ObjetivoDepositoController {

    private final ObjetivoDepositoService depositoService;


    @PostMapping("/{id}/movimentar")
    public ResponseEntity<ObjetivoMovimentacaoDTO> movimentar(
            @PathVariable Long id,
            @RequestBody ObjetivoMovimentacaoRequestDTO dto
    ) {

        return ResponseEntity.ok(
                depositoService.movimentar(
                        id,
                        dto.valor(),
                        dto.tipo()
                )
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


    @PutMapping("/movimentacoes/{movimentacaoId}")
    public ResponseEntity<ObjetivoMovimentacaoDTO> editar(
            @PathVariable Long movimentacaoId,
            @RequestBody ObjetivoMovimentacaoRequestDTO dto
    ) {

        return ResponseEntity.ok(
                depositoService.editar(
                        movimentacaoId,
                        dto.valor()
                )
        );
    }


    @DeleteMapping("/movimentacoes/{movimentacaoId}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long movimentacaoId
    ) {

        depositoService.deletar(movimentacaoId);

        return ResponseEntity.noContent().build();
    }
}