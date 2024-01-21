package com.nelumbo.parqueadero.repositories;

import com.nelumbo.parqueadero.entities.ParqueaderoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParqueaderoVehiculoRepository extends JpaRepository<ParqueaderoVehiculo, Long> {
}
