package TCC.ControleFincanceiro.dto.usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedefinirSenhaDTO {

    private String token;

    private String novaSenha;

}