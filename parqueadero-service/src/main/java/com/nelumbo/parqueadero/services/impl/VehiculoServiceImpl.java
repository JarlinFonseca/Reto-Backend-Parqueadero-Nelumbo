package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.entities.ParqueaderoVehiculo;
import com.nelumbo.parqueadero.entities.Vehiculo;
import com.nelumbo.parqueadero.repositories.ParqueaderoVehiculoRepository;
import com.nelumbo.parqueadero.repositories.VehiculoRepository;
import com.nelumbo.parqueadero.services.IVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculoServiceImpl implements IVehiculoService {
    private final VehiculoRepository vehiculoRepository;
    private final ParqueaderoVehiculoRepository parqueaderoVehiculoRepository;

    @Override
    public Vehiculo guardarVehiculo(String placa, Vehiculo vehiculo) {
        if(vehiculo==null){
            Vehiculo vehiculoNuevo = new Vehiculo();
            vehiculoNuevo.setPlaca(placa);
            vehiculoRepository.save(vehiculoNuevo);
            return  vehiculoNuevo;
        }
        return vehiculo;
    }

    @Override
    public Vehiculo obtenerVehiculoPorPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa).orElse(null);
    }

    @Override
    public Vehiculo obtenerVehiculoPorId(Long id) {
        return vehiculoRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean verificarExistenciaVehiculo(Vehiculo vehiculo) {
        if (vehiculo==null) return false;
        ParqueaderoVehiculo parqueaderoVehiculo = parqueaderoVehiculoRepository.findByVehiculo_idAndFlagIngresoActivo(vehiculo.getId(), true).orElse(null);
        return parqueaderoVehiculo!=null;
    }
}
