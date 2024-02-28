package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.entities.Vehiculo;

public interface IVehiculoService {
    Vehiculo guardarVehiculo(String placa, Vehiculo vehiculo);
    Vehiculo obtenerVehiculoPorPlaca(String placa);
    Vehiculo obtenerVehiculoPorId(Long id);
    Boolean verificarExistenciaVehiculo(Vehiculo vehiculo);
}
