package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.comprovante.ComprovanteResponseDTO;
import TCC.ControleFincanceiro.dto.ia.TransacaoIADTO;
import TCC.ControleFincanceiro.service.ComprovanteService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/comprovantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ComprovanteController {

    private final ComprovanteService service;


    /* ===================================================
       UPLOAD ANTIGO
    =================================================== */

    @PostMapping("/upload")
    public ComprovanteResponseDTO upload(

            @RequestParam MultipartFile file,

            @RequestParam Long usuarioId

    ) {

        return service.upload(
                file,
                usuarioId
        );
    }


    /* ===================================================
       ANALISAR ARQUIVO PARA PREENCHER O FORM
    =================================================== */

    @PostMapping(
            value = "/analisar-arquivo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TransacaoIADTO> analisarArquivo(

            @RequestPart("comprovante")
            MultipartFile comprovante

    ) {

        return ResponseEntity.ok(

                service.analisarArquivo(
                        comprovante
                )

        );
    }

}