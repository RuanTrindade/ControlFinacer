package TCC.ControleFincanceiro.controller;


import TCC.ControleFincanceiro.dto.transacao.TransacaoAtualizarDTO;
import TCC.ControleFincanceiro.dto.transacao.TransacaoCriarDTO;
import TCC.ControleFincanceiro.dto.transacao.TransacaoResumoDTO;
import TCC.ControleFincanceiro.service.TransacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<TransacaoResumoDTO> criarTransacao(@RequestBody TransacaoCriarDTO dto) {
        TransacaoResumoDTO nova = transacaoService.criarTransacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResumoDTO> atualizarTransacao(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestBody TransacaoAtualizarDTO dto
    ) {
        return ResponseEntity.ok(
                transacaoService.atualizarTransacao(id, usuarioId, dto)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResumoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(transacaoService.listarPorUsuario(usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        transacaoService.deletarTransacao(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
