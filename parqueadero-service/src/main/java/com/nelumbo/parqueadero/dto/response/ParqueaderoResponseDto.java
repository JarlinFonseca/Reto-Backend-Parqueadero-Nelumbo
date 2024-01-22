package com.nelumbo.parqueadero.dto.response;

import com.nelumbo.parqueadero.entities.Usuario;
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
    private Usuario usuario;
}