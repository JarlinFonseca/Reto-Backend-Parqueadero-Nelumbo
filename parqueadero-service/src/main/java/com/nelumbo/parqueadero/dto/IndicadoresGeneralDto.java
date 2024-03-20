package com.nelumbo.parqueadero.dto;

import com.nelumbo.parqueadero.dto.response.GananciasResponseDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoResponseDto;
import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IndicadoresGeneralDto {
   private List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez;
   private List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos;
   private  List<IndicadorVehiculosMasVecesRegistradoResponseDto>  indicadorVehiculosMasVecesRegistrado;
   private List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia;
   private GananciasResponseDto indicadorGanancias;
   private String nombreParqueadero;
   private String placa;
   private String tokenBearer;
}
