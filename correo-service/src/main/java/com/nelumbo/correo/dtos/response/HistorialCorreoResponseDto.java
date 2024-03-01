package com.nelumbo.correo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HistorialCorreoResponseDto {
    private List<MensajeInfoResponseDto> correos;
    private Long cantidadEnviados;
}
