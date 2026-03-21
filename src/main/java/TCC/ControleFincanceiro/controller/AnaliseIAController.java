package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.ia.AnaliseIADTO;
import TCC.ControleFincanceiro.service.AnaliseIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class AnaliseIAController {

    private final AnaliseIAService service;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<AnaliseIADTO> analisar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.analisar(usuarioId));
    }
}