package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.investimento.InvestimentoMovimentacaoDTO;
import TCC.ControleFincanceiro.dto.investimento.InvestimentoMovimentacaoRequestDTO;
import TCC.ControleFincanceiro.entity.enumerated.TipoInvestimento;
import TCC.ControleFincanceiro.service.InvestimentoMovimentacaoService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/investimentos")
@RequiredArgsConstructor
public class InvestimentoMovimentacaoController {


    private final InvestimentoMovimentacaoService movimentacaoService;


    /* ===================================================
       REGISTRAR MOVIMENTAÇÃO
    =================================================== */

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<InvestimentoMovimentacaoDTO> registrarMovimentacao(

            @PathVariable Long id,

            @RequestBody InvestimentoMovimentacaoRequestDTO dto

    ) {

        return ResponseEntity.ok(

                movimentacaoService.registrarMovimentacao(

                        id,

                        dto.valor(),

                        dto.tipo(),

                        dto.referencia()

                )

        );
    }


    /* ===================================================
       LISTAR MOVIMENTAÇÕES
    =================================================== */

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<Page<InvestimentoMovimentacaoDTO>>
    listarMovimentacoes(

            @PathVariable Long id,

            @RequestParam(required = false)
            Integer mes,

            @RequestParam(required = false)
            Integer ano,

            @RequestParam(required = false)
            TipoInvestimento tipo,

            Pageable pageable

    ) {

        return ResponseEntity.ok(

                movimentacaoService.listarMovimentacoes(

                        id,

                        mes,

                        ano,

                        tipo,

                        pageable

                )

        );
    }


    /* ===================================================
       EDITAR MOVIMENTAÇÃO
    =================================================== */

    @PutMapping("/movimentacoes/{movimentacaoId}")
    public ResponseEntity<InvestimentoMovimentacaoDTO> editarMovimentacao(

            @PathVariable Long movimentacaoId,

            @RequestBody InvestimentoMovimentacaoRequestDTO dto

    ) {

        return ResponseEntity.ok(

                movimentacaoService.editarMovimentacao(

                        movimentacaoId,

                        dto.valor(),

                        dto.referencia()

                )

        );
    }


    /* ===================================================
       EXCLUIR MOVIMENTAÇÃO
    =================================================== */

    @DeleteMapping("/movimentacoes/{movimentacaoId}")
    public ResponseEntity<Void> deletarMovimentacao(

            @PathVariable Long movimentacaoId

    ) {

        movimentacaoService.deletarMovimentacao(
                movimentacaoId
        );


        return ResponseEntity
                .noContent()
                .build();
    }

}