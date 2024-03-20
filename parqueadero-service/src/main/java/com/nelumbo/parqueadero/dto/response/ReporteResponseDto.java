package com.nelumbo.parqueadero.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReporteResponseDto {
    private List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez;
    private List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos;
    private List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado;
    private List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia;
    private GananciasResponseDto gananciasResponseDto;
    private String nombreParqueadero;
    private String placa;
    private String tokenJwt;
    private Date fechaSolicitud;
}
