package com.nelumbo.parqueadero.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VehiculoParqueadoResponseDto {
    private Long id;
    private String placa;
    private Date fechaIngreso;
}
