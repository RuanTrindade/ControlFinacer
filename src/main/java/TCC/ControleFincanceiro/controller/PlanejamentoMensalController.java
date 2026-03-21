package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoAtualizarDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCriarDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoMensalResumoDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoResumoDTO;
import TCC.ControleFincanceiro.entity.PlanejamentoMensal;
import TCC.ControleFincanceiro.service.PlanejamentoMensalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planejamentos")
@RequiredArgsConstructor
public class PlanejamentoMensalController {

    private final PlanejamentoMensalService planejamentoService;


    @PostMapping
    public ResponseEntity<PlanejamentoResumoDTO> criar(@RequestBody PlanejamentoCriarDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planejamentoService.criar(dto));
    }


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PlanejamentoResumoDTO>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(planejamentoService.listar(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanejamentoResumoDTO> atualizar(
            @PathVariable Long id,
            @RequestBody PlanejamentoAtualizarDTO dto
    ) {
        return ResponseEntity.ok(planejamentoService.atualizar(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        planejamentoService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/copiar")
    public ResponseEntity<PlanejamentoResumoDTO> copiar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        LocalDate novaReferencia = LocalDate.parse(body.get("novaReferencia"));

        return ResponseEntity.ok(
                planejamentoService.copiar(id, novaReferencia)
        );
    }


    @GetMapping("/{id}/resumo")
    public ResponseEntity<PlanejamentoMensalResumoDTO> resumo(@PathVariable Long id) {
        return ResponseEntity.ok(planejamentoService.resumoMensal(id));
    }
}