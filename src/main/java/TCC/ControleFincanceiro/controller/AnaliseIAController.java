package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.ia.AnaliseIADTO;
import TCC.ControleFincanceiro.service.AnaliseIAService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class AnaliseIAController {


    private final AnaliseIAService service;


    @GetMapping
    public ResponseEntity<AnaliseIADTO> analisar(
            @RequestParam Long usuarioId,
            @RequestParam int mes,
            @RequestParam int ano

    ) {

        return ResponseEntity.ok(
                service.analisar(
                        usuarioId,
                        mes,
                        ano
                )
        );

    }

}