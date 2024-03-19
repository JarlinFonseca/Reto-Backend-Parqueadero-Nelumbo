package com.nelumbo.parqueadero.repositories;

import com.nelumbo.parqueadero.entities.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query(nativeQuery = true, value = "select r.archivo_nombre from reportes r "+
            "where r.usuario_id = :usuarioId " +
            "order by r.fecha_creado desc, r.fecha_guardado desc, r.incrementador desc " +
            "limit 1")
    Optional<String> findUltimoReporteNombreArchivoPorUsuarioId(Long usuarioId);

    @Query(nativeQuery = true, value = "select r.fecha_guardado from reportes r "+
            "where r.usuario_id = :usuarioId " +
            "order by r.fecha_creado desc, r.fecha_guardado desc, r.incrementador desc " +
            "limit 1")
    Optional<Date> findFechaGuardadoUltimoReportePorUsuarioId(Long usuarioId);
}
