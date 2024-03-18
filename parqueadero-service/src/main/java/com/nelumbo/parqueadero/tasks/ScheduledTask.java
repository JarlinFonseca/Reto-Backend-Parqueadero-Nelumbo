package com.nelumbo.parqueadero.tasks;

import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.exception.UsuarioSocioNoAutenticadoException;
import com.nelumbo.parqueadero.services.*;
import com.nelumbo.parqueadero.services.impl.ReporteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final IReporteService reporteService;
    private final IExcelService excelService;
    private final IHistorialService historialService;
    private final IParqueaderoVehiculoService parqueaderoVehiculoService;
    private final IParqueaderoService parqueaderoService;
    private final IToken token;

    @Scheduled(fixedRate = 30000) // Ejecutar cada 5 minutos
    public void processColaSolicitudes() {
        Queue<CompletableFuture<ReporteResponse2Dto>> colaSolicitudes = reporteService.getColaSolicitudes();

        // Procesar hasta 2 solicitudes en paralelo
        for (int i = 0; i < 2; i++) {
            CompletableFuture<ReporteResponse2Dto> future = colaSolicitudes.poll();
            if (future != null) {
                future.thenAccept(reporteResponseDto -> {

                    System.out.println(reporteResponseDto.getGananciasResponseDto());

                    try {
                        excelService.generarExcelReporte(
                                reporteResponseDto.getIndicadorVehiculoParqueadoPrimeraVez(),
                                reporteResponseDto.getIndicadorVehiculosMasVecesRegistradosDiferentesParqueaderos(),
                                reporteResponseDto.getIndicadorVehiculosMasVecesRegistrado(),
                                reporteResponseDto.getVehiculosPorCoincidencia(),
                                reporteResponseDto.getGananciasResponseDto(),
                                reporteResponseDto.getNombreParqueadero(),
                                reporteResponseDto.getPlaca()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    // Aquí puedes realizar alguna acción con el reporte generado, si lo deseas
                    // Por ejemplo, enviarlo por correo electrónico o almacenarlo en una base de datos
                    System.out.println("Reporte generado: " + reporteResponseDto);
                });
            } else {
                break; // Si no hay más solicitudes en la cola, sal del bucle
            }
        }
    }
}


