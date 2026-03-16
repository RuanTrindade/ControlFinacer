package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.categoria.CategoriaAtualizarDTO;
import TCC.ControleFincanceiro.dto.categoria.CategoriaCriarDTO;
import TCC.ControleFincanceiro.dto.categoria.CategoriaResumoDTO;
import TCC.ControleFincanceiro.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResumoDTO> criarCategoria(@RequestBody CategoriaCriarDTO dto) {
        CategoriaResumoDTO nova = categoriaService.criarCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResumoDTO> atualizarCategoria(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestBody CategoriaAtualizarDTO dto
    ) {
        return ResponseEntity.ok(
                categoriaService.atualizarCategoria(id, usuarioId, dto)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CategoriaResumoDTO>> listarCategorias(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(categoriaService.listarCategorias(usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        categoriaService.deletarCategoria(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}