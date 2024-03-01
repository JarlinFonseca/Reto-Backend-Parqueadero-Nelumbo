package com.nelumbo.correo.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensajeInfoResponseDto {

    private String id;
    private String email;
    private String placa;
    private String descripcion;
    private String parqueaderoNombre;
    private String fechaEnviado;
}
