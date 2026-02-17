package TCC.ControleFincanceiro.controller;


import TCC.ControleFincanceiro.dto.TransacaoCriarDTO;
import TCC.ControleFincanceiro.dto.TransacaoResumoDTO;
import TCC.ControleFincanceiro.entity.Transacao;
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
    public ResponseEntity<Transacao> criarTransacao(@RequestBody TransacaoCriarDTO dto) {
        Transacao nova = transacaoService.criarTransacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResumoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(transacaoService.listarPorUsuario(usuarioId));
    }
}
