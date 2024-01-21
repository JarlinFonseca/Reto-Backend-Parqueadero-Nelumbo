package com.nelumbo.parqueadero.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginResponseDto {
    private String mensaje;
    private String token;
}
