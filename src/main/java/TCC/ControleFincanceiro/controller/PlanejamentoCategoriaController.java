package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResponseDTO;
import TCC.ControleFincanceiro.dto.planejamento.PlanejamentoCategoriaResumoDTO;
import TCC.ControleFincanceiro.entity.PlanejamentoCategoria;
import TCC.ControleFincanceiro.service.PlanejamentoCategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/planejamento-categorias")
@RequiredArgsConstructor
public class PlanejamentoCategoriaController {

    private final PlanejamentoCategoriaService categoriaService;


    @PostMapping
    public ResponseEntity<PlanejamentoCategoriaResponseDTO> salvar(
            @RequestBody PlanejamentoCategoriaDTO dto
    ) {
        return ResponseEntity.ok(categoriaService.salvar(dto));
    }


    @GetMapping("/{planejamentoId}")
    public ResponseEntity<List<PlanejamentoCategoriaResponseDTO>> listar(
            @PathVariable Long planejamentoId
    ) {
        return ResponseEntity.ok(categoriaService.listar(planejamentoId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{planejamentoId}/resumo")
    public ResponseEntity<List<PlanejamentoCategoriaResumoDTO>> resumo(
            @PathVariable Long planejamentoId
    ) {
        return ResponseEntity.ok(categoriaService.resumo(planejamentoId));
    }
}