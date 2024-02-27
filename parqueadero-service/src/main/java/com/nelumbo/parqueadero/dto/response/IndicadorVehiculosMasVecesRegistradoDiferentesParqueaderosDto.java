package com.nelumbo.parqueadero.dto.response;

import com.nelumbo.parqueadero.entities.Vehiculo;
import lombok.Data;

@Data
public class IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto {
    private Vehiculo vehiculo;
    private Long cantidadVecesRegistrado;
}
