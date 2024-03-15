package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.services.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteRestController {

    private final IReporteService reporteService;

    @GetMapping("/exportar/{id}")
    public ResponseEntity<String> exportarExcel(@PathVariable(name="id")Long parqueaderoId) {
        try {
            reporteService.generarReporte(parqueaderoId);
            return ResponseEntity.ok("Archivo Excel generado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al generar el archivo Excel: " + e.getMessage());
        }
    }
}
