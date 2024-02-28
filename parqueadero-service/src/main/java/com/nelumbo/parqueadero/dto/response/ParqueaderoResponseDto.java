package com.nelumbo.parqueadero.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ParqueaderoResponseDto {
    private Long id;
    private String nombre;
    private Long cantidadVehiculosMaximo;
    private Double costoHoraVehiculo;
    private Date fechaRegistro;
    private Long socioId;
}