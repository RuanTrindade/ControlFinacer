package TCC.ControleFincanceiro.dto.categoria;

import TCC.ControleFincanceiro.entity.enumerated.TipoTransacao;

public record CategoriaResumoDTO(
        Long id,
        String nome,
        String cor,
        String icone,
        TipoTransacao tipo
) {}