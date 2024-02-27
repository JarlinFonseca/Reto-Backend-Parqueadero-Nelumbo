package com.nelumbo.parqueadero.dto.response;

import com.nelumbo.parqueadero.entities.Rol;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String documentoDeIdentidad;
    private String correo;
    private Date fechaRegistro;
    private String rol;
}
