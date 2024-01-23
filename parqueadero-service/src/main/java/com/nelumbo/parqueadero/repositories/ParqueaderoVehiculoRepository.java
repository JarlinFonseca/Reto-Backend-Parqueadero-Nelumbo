package com.nelumbo.parqueadero.repositories;

import com.nelumbo.parqueadero.entities.ParqueaderoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParqueaderoVehiculoRepository extends JpaRepository<ParqueaderoVehiculo, Long> {

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM parqueaderos_vehiculos " +
            "WHERE parqueadero_id=?1 AND flag_ingreso_activo=true")
    Long getCountVehiculosParqueadero(Long parqueaderoId);

    Optional<ParqueaderoVehiculo> findByVehiculo_idAndFlagIngresoActivo(Long vehiculoId, Boolean flagIngresoActivo);

    Optional<List<ParqueaderoVehiculo>> findAllByParqueadero_idAndFlagIngresoActivo(Long parqueaderoId, Boolean flagIngresoActivo);

    @Query(nativeQuery = true, value = "SELECT vehiculo_id, parqueadero_id, COUNT(vehiculo_id) AS cantidadVecesRegistrado " +
            "FROM parqueaderos_vehiculos " +
            "GROUP BY vehiculo_id, parqueadero_id " +
            "ORDER BY cantidadVecesRegistrado DESC " +
            "LIMIT 10")
    Optional<List<Object[]>> obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez();

    @Query(nativeQuery = true, value = "SELECT vehiculo_id, parqueadero_id, COUNT(vehiculo_id) AS cantidadVecesRegistrado " +
            "FROM parqueaderos_vehiculos " +
            "WHERE parqueadero_id = :parqueaderoId " +
            "GROUP BY vehiculo_id, parqueadero_id " +
            "ORDER BY cantidadVecesRegistrado DESC " +
            "LIMIT 10")
    Optional<List<Object[]>> obtenerVehiculosMasVecesRegistradosEnUnParqueaderoLimiteDiez(Long parqueaderoId);
}
