package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.dto.IndicadoresGeneralDto;
import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteRestController {

    private final IReporteService reporteService;
    private final IAWSS3Service awss3Service;

    @GetMapping("/parqueaderos/{id}/vehiculos/placa/{placa}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SOCIO')")
    public ResponseEntity<ReporteResponseAsyncDto> generarReporteAsync(@PathVariable(name="id")Long parqueaderoId, @PathVariable(name="placa")String placa)  {
        IndicadoresGeneralDto indicadoresGeneralDto = reporteService.obtenerTodosIndicadores(parqueaderoId, placa);
        reporteService.generarReporteIndicadores(indicadoresGeneralDto);
        return ResponseEntity.accepted().body(new ReporteResponseAsyncDto("La solicitud de reporte se ha recibido y se está procesando en segundo plano."));
    }

    @GetMapping("/ultimo-reporte")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SOCIO')")
    public ResponseEntity<Resource> descargarUltimoReporte(){
        InputStreamResource resource = new InputStreamResource(reporteService.descargarUltimoReporte());
        String nombreArchivo = awss3Service.getNameFileS3(); // Reemplaza esto con el método real que obtiene el nombre del archivo

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/solicitudes/fecha")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SOCIO')")
    public ResponseEntity<FechaReporteResponseDto> getFechaUltimoReporte(){
        return ResponseEntity.ok(reporteService.obtenerFechaUltimoReporte());
    }

}
