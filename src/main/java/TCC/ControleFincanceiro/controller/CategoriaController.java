package TCC.ControleFincanceiro.controller;

import TCC.ControleFincanceiro.dto.categoria.CategoriaAtualizarDTO;
import TCC.ControleFincanceiro.dto.categoria.CategoriaCriarDTO;
import TCC.ControleFincanceiro.dto.categoria.CategoriaResumoDTO;

import TCC.ControleFincanceiro.entity.enumerated.TipoTransacao;
import TCC.ControleFincanceiro.service.CategoriaService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@CrossOrigin(
        origins =
                "http://localhost:5173"
)
public class CategoriaController {


    private final CategoriaService categoriaService;


    /* ===================================================
       CRIAR
    =================================================== */

    @PostMapping
    public ResponseEntity<CategoriaResumoDTO>
    criarCategoria(

            @RequestBody
            CategoriaCriarDTO dto

    ) {

        CategoriaResumoDTO nova =
                categoriaService
                        .criarCategoria(
                                dto
                        );


        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        nova
                );

    }


    /* ===================================================
       ATUALIZAR
    =================================================== */

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResumoDTO>
    atualizarCategoria(

            @PathVariable
            Long id,

            @RequestParam
            Long usuarioId,

            @RequestBody
            CategoriaAtualizarDTO dto

    ) {

        return ResponseEntity.ok(

                categoriaService
                        .atualizarCategoria(
                                id,
                                usuarioId,
                                dto
                        )

        );

    }


    /* ===================================================
       LISTAGEM PAGINADA
       Tela principal de Categorias
    =================================================== */

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<CategoriaResumoDTO>> listarCategorias(
            @PathVariable Long usuarioId,
            @RequestParam TipoTransacao tipo,
            @PageableDefault(size = 8) Pageable pageable
    ) {

        return ResponseEntity.ok(
                categoriaService.listarCategoriasPaginado(
                        usuarioId,
                        tipo,
                        pageable
                )
        );
    }


    /* ===================================================
       TODAS AS CATEGORIAS
       Formulários / selects
    =================================================== */

    @GetMapping(
            "/usuario/{usuarioId}/todas"
    )
    public ResponseEntity<
            List<CategoriaResumoDTO>
            >
    listarTodasCategorias(

            @PathVariable
            Long usuarioId

    ) {

        return ResponseEntity.ok(

                categoriaService
                        .listarCategorias(
                                usuarioId
                        )

        );

    }


    /* ===================================================
       DELETAR
    =================================================== */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deletarCategoria(

            @PathVariable
            Long id,

            @RequestParam
            Long usuarioId

    ) {

        categoriaService
                .deletarCategoria(
                        id,
                        usuarioId
                );


        return ResponseEntity
                .noContent()
                .build();

    }

}