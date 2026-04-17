package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.comprovante.ComprovanteResponseDTO;
import TCC.ControleFincanceiro.entity.Comprovante;
import TCC.ControleFincanceiro.service.ComprovanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/comprovantes")
@RequiredArgsConstructor
public class ComprovanteController {

    private final ComprovanteService service;

    // ==============================
    // UPLOAD
    // ==============================
    @PostMapping("/upload")
    public ComprovanteResponseDTO upload(@RequestParam MultipartFile file,
                                         @RequestParam Long usuarioId) {

        return service.upload(file, usuarioId);
    }

    // ==============================
    // ANALISAR
    // ==============================
    @PostMapping("/{id}/analisar")
    public String analisar(@PathVariable Long id) {

        return service.analisar(id);
    }
}