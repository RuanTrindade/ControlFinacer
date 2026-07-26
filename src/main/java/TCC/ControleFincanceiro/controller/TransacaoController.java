package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.transacao.TransacaoAtualizarDTO;
import TCC.ControleFincanceiro.dto.transacao.TransacaoCriarDTO;
import TCC.ControleFincanceiro.dto.transacao.TransacaoResumoDTO;
import TCC.ControleFincanceiro.entity.enumerated.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.StatusPagamento;
import TCC.ControleFincanceiro.service.TransacaoService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TransacaoController {

    private final TransacaoService transacaoService;



    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TransacaoResumoDTO> criarTransacao(

            @RequestPart("dados")
            TransacaoCriarDTO dto,

            @RequestPart(
                    value = "comprovante",
                    required = false
            )
            MultipartFile comprovante

    ) {

        TransacaoResumoDTO nova =
                transacaoService.criarTransacao(
                        dto,
                        comprovante
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nova);
    }


    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TransacaoResumoDTO> atualizarTransacao(

            @PathVariable Long id,

            @RequestParam Long usuarioId,

            @RequestPart("dados")
            TransacaoAtualizarDTO dto,

            @RequestPart(
                    value = "comprovante",
                    required = false
            )
            MultipartFile comprovante

    ) {

        return ResponseEntity.ok(
                transacaoService.atualizarTransacao(
                        id,
                        usuarioId,
                        dto,
                        comprovante
                )
        );
    }



    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<TransacaoResumoDTO>> listarPorUsuario(

            @PathVariable Long usuarioId,

            @RequestParam Integer mes,

            @RequestParam Integer ano,

            Pageable pageable

    ) {

        return ResponseEntity.ok(
                transacaoService.listarPorUsuario(
                        usuarioId,
                        mes,
                        ano,
                        pageable
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(

            @PathVariable Long id,

            @RequestParam Long usuarioId

    ) {

        transacaoService.deletarTransacao(
                id,
                usuarioId
        );

        return ResponseEntity.noContent().build();
    }



    @GetMapping("/saldo/{usuarioId}")
    public ResponseEntity<BigDecimal> saldo(

            @PathVariable Long usuarioId

    ) {

        return ResponseEntity.ok(
                transacaoService.obterSaldoUsuario(
                        usuarioId
                )
        );
    }



    @GetMapping("/filtrar")
    public ResponseEntity<Page<TransacaoResumoDTO>>
    filtrarTransacoes(

            @RequestParam
            Long usuarioId,

            @RequestParam(required = false)
            Long categoriaId,

            @RequestParam(required = false)
            StatusPagamento status,

            @RequestParam(required = false)
            MetodoPagamento metodoPagamento,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate dataFim,

            @RequestParam(required = false)
            BigDecimal valorMin,

            @RequestParam(required = false)
            BigDecimal valorMax,

            @RequestParam(required = false)
            String descricao,

            Pageable pageable
    ) {

        Page<TransacaoResumoDTO> transacoes =
                transacaoService.filtrarTransacoes(
                        usuarioId,
                        categoriaId,
                        status,
                        metodoPagamento,
                        dataInicio,
                        dataFim,
                        valorMin,
                        valorMax,
                        descricao,
                        pageable
                );

        return ResponseEntity.ok(transacoes);
    }
}