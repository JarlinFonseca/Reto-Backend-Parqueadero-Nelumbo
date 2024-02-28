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

    @Query(nativeQuery = true, value = "SELECT vehiculo_id, COUNT(vehiculo_id) AS cantidadVecesRegistrado " +
            "FROM parqueaderos_vehiculos " +
            "GROUP BY vehiculo_id " +
            "ORDER BY cantidadVecesRegistrado DESC " +
            "LIMIT 10")
    Optional<List<Object[]>> obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiezAdmin();

    @Query(nativeQuery = true, value = "SELECT vehiculo_id, COUNT(vehiculo_id) AS cantidadVecesRegistrado " +
            "FROM parqueaderos_vehiculos pv join parqueaderos p ON(pv.parqueadero_id=p.id) " +
            "join usuarios u ON(p.usuario_id=u.id) " +
            "where u.id = :usuarioId " +
            "group by vehiculo_id "+
            "ORDER BY cantidadVecesRegistrado DESC " +
            "LIMIT 10")
    Optional<List<Object[]>> obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiezSocio(Long usuarioId);

    @Query(nativeQuery = true, value = "SELECT vehiculo_id, parqueadero_id, COUNT(vehiculo_id) AS cantidadVecesRegistrado " +
            "FROM parqueaderos_vehiculos " +
            "WHERE parqueadero_id = :parqueaderoId " +
            "GROUP BY vehiculo_id, parqueadero_id " +
            "ORDER BY cantidadVecesRegistrado DESC " +
            "LIMIT 10")
    Optional<List<Object[]>> obtenerVehiculosMasVecesRegistradosEnUnParqueaderoLimiteDiez(Long parqueaderoId);


    @Query(nativeQuery = true, value = "SELECT parqueadero_id, vehiculo_id, fecha_ingreso  FROM parqueaderos_vehiculos " +
            "WHERE parqueadero_id =?1 AND " +
            "vehiculo_id NOT in(select pv.vehiculo_id  from historial h join parqueaderos_vehiculos pv on(h.parqueadero_vehiculo_id=pv.id) " +
            "GROUP by pv.vehiculo_id ) AND vehiculo_id IN " +
            "(SELECT vehiculo_id FROM parqueaderos_vehiculos " +
            "WHERE parqueadero_id = ?1 " +
            "GROUP BY vehiculo_id " +
            "HAVING COUNT(vehiculo_id) = 1);")
    Optional<List<Object[]>> obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(Long parqueaderoId);


    @Query(value = "SELECT pv.id, v.placa, pv.fecha_ingreso " +
            "FROM parqueaderos_vehiculos pv JOIN vehiculos v " +
            "ON (pv.vehiculo_id=v.id) " +
            "WHERE pv.flag_ingreso_activo=true AND v.placa like %:placa%", nativeQuery = true)
    Optional<List<Object[]>> getVehiculosParqueadosPorCoincidencia(String placa);

    @Query(value = "SELECT pv.id, v.placa, pv.fecha_ingreso " +
            "FROM parqueaderos_vehiculos pv JOIN vehiculos v " +
            "ON (pv.vehiculo_id=v.id) " +
            "WHERE pv.flag_ingreso_activo=true AND v.placa like %:placa% " +
            "and pv.parqueadero_id IN :idsParqueaderos", nativeQuery = true)
    Optional<List<Object[]>> getVehiculosParqueadosPorCoincidenciaSocio(String placa, List<Long> idsParqueaderos);
}
