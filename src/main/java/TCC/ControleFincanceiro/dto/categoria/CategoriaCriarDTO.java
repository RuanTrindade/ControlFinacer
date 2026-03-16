package TCC.ControleFincanceiro.dto.categoria;

import TCC.ControleFincanceiro.entity.enumerated.TipoTransacao;

public record CategoriaCriarDTO(
        Long usuarioId,
        String nome,
        String cor,
        String icone,
        TipoTransacao tipo
) {}