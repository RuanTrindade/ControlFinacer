package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.relatorio.RelatorioDTO;
import TCC.ControleFincanceiro.service.RelatorioService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/relatorio")
@RequiredArgsConstructor
public class RelatorioController {


    private final RelatorioService relatorioService;


    @GetMapping
    public ResponseEntity<RelatorioDTO> relatorio(

            @RequestParam Long usuarioId,

            @RequestParam int mes,

            @RequestParam int ano

    ) {

        return ResponseEntity.ok(

                relatorioService.gerarRelatorio(

                        usuarioId,

                        mes,

                        ano

                )

        );

    }

}