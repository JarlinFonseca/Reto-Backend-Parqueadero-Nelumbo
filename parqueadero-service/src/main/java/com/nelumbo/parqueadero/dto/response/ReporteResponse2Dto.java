package com.nelumbo.parqueadero.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReporteResponse2Dto {
    private List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez;
    private List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos;
    private List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado;
    private List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia;
    private GananciasResponseDto gananciasResponseDto;
    private String nombreParqueadero;
    private String placa;
}
