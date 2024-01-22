package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.entities.Vehiculo;

public interface IVehiculoService {
    Vehiculo guardarVehiculo(String placa);
    Vehiculo obtenerVehiculoPorPlaca(String placa);
    Vehiculo obtenerVehiculoPorId(Long id);
    Boolean verificarExistenciaVehiculo(String placa);
}
