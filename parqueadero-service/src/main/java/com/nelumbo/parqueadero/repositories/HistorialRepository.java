package com.nelumbo.parqueadero.repositories;

import com.nelumbo.parqueadero.entities.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {
    @Query(value = "SELECT SUM(h.pago_total) " +
            "FROM parqueaderos_vehiculos pv JOIN historial h " +
            "ON(pv.id=h.parqueadero_vehiculo_id) " +
            "WHERE pv.parqueadero_id = :parqueaderoId " +
            "AND (SELECT EXTRACT(WEEK FROM h.fecha_salida) AS semana_fecha_especifica) = :semanaActual " +
            "AND EXTRACT(MONTH FROM h.fecha_salida) = :mes " +
            "AND EXTRACT(YEAR FROM h.fecha_salida) = :anio", nativeQuery = true)
    Long obtenerGananciasSemana(Long parqueaderoId, Long semanaActual, Long mes, Long anio);


    @Query(value = "SELECT SUM(h.pago_total) " +
            "FROM parqueaderos_vehiculos pv JOIN historial h " +
            "ON(pv.id=h.parqueadero_vehiculo_id) " +
            "WHERE pv.parqueadero_id = :parqueaderoId " +
            "AND EXTRACT(MONTH FROM h.fecha_salida) = :mes " +
            "AND EXTRACT(YEAR FROM h.fecha_salida) = :anio", nativeQuery = true)
    Long obtenerGananciasMes(Long parqueaderoId, Long mes, Long anio);

    @Query(nativeQuery = true, value = "SELECT SUM(h.pago_total) " +
            "FROM parqueaderos_vehiculos pv JOIN historial h " +
            "ON(pv.id=h.parqueadero_vehiculo_id) " +
            "WHERE pv.parqueadero_id = :parqueaderoId " +
            "AND EXTRACT(YEAR FROM h.fecha_salida) = :anio")
    Long obtenerGananciasAnio(Long parqueaderoId, Long anio);


    @Query(value = "SELECT SUM(h.pago_total) " +
            "FROM parqueaderos_vehiculos pv JOIN historial h " +
            "ON(pv.id=h.parqueadero_vehiculo_id) " +
            "WHERE pv.parqueadero_id = :parqueaderoId AND TO_CHAR(fecha_salida, 'YYYY-MM-DD') LIKE :fechaHoy", nativeQuery = true)
    Long obtenerGananciasHoy(Long parqueaderoId, String fechaHoy);

}
