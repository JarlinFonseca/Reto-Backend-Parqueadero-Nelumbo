package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.request.IngresoVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.request.SalidaVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.IngresoVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.SalidaVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.entities.ParqueaderoVehiculo;

public interface IParqueaderoVehiculoService {

    IngresoVehiculoParqueaderoResponseDto registrarIngreso(IngresoVehiculoParqueaderoRequestDto parqueaderoRequestDto);

    SalidaVehiculoParqueaderoResponseDto registrarSalida(SalidaVehiculoParqueaderoRequestDto salidaVehiculoParqueaderoRequestDto);
    ParqueaderoVehiculo obtenerParqueaderoVehiculoPorIdYFlagVehiculoActivo(Long id, Boolean flag);

}
