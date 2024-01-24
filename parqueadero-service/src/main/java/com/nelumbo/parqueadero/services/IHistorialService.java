package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.entities.Historial;

import java.util.List;

public interface IHistorialService {

    void guardarHistorial(Historial historial);

    List<VehiculoParqueadoResponseDto> obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(Long parqueaderoId);
}
