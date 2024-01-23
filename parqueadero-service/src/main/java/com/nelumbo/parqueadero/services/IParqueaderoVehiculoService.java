package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.request.IngresoVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.request.SalidaVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoResponseDto;
import com.nelumbo.parqueadero.dto.response.IngresoVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.SalidaVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.entities.ParqueaderoVehiculo;

import java.util.List;

public interface IParqueaderoVehiculoService {

    IngresoVehiculoParqueaderoResponseDto registrarIngreso(IngresoVehiculoParqueaderoRequestDto parqueaderoRequestDto);

    SalidaVehiculoParqueaderoResponseDto registrarSalida(SalidaVehiculoParqueaderoRequestDto salidaVehiculoParqueaderoRequestDto);
    ParqueaderoVehiculo obtenerParqueaderoVehiculoPorIdYFlagVehiculoActivo(Long id, Boolean flag);
    List<VehiculoParqueadoResponseDto> obtenerVehiculosParqueadosPorIdParqueadero(Long parqueaderoId);
    List<VehiculoParqueadoResponseDto> obtenerVehiculosParqueaderosAsociadosPorId(Long parqueaderoId);

    List<IndicadorVehiculosMasVecesRegistradoResponseDto> obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez();

    List<IndicadorVehiculosMasVecesRegistradoResponseDto> obtenerVehiculosMasVecesRegistradosParqueaderoPorId(Long parqueaderoId);

}
