package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.objetivo.ObjetivoAtualizarDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoCriarDTO;
import TCC.ControleFincanceiro.dto.objetivo.ObjetivoResumoDTO;
import TCC.ControleFincanceiro.service.ObjetivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/objetivos")
@RequiredArgsConstructor
public class ObjetivoController {

    private final ObjetivoService objetivoService;

    @PostMapping
    public ResponseEntity<ObjetivoResumoDTO> criar(@RequestBody ObjetivoCriarDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(objetivoService.criar(dto));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<ObjetivoResumoDTO>> listar(

            @PathVariable Long usuarioId,

            @RequestParam Boolean finalizado,

            Pageable pageable

    ) {

        return ResponseEntity.ok(
                objetivoService.listar(
                        usuarioId,
                        finalizado,
                        pageable
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoResumoDTO> atualizar(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestBody ObjetivoAtualizarDTO dto
    ) {
        return ResponseEntity.ok(
                objetivoService.atualizar(id, usuarioId, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        objetivoService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoResumoDTO> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                objetivoService.buscarPorId(id)
        );
    }
}