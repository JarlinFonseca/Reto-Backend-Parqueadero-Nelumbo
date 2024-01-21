package com.nelumbo.parqueadero.repositories;

import com.nelumbo.parqueadero.entities.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
}
