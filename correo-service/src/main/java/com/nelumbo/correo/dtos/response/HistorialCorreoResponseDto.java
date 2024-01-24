package com.nelumbo.correo.dtos.response;

import com.nelumbo.correo.entities.Mensaje;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HistorialCorreoResponseDto {
    private List<Mensaje> correos;
    private Long cantidadEnviados;
}
