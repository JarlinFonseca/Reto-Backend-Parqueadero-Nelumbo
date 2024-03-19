/*
package com.nelumbo.parqueadero.tasks;


import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.entities.Reporte;
import com.nelumbo.parqueadero.entities.Usuario;
import com.nelumbo.parqueadero.repositories.ReporteRepository;
import com.nelumbo.parqueadero.repositories.UsuarioRepository;
import com.nelumbo.parqueadero.services.*;
import com.nelumbo.parqueadero.utils.FechaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
public class ScheduledTask2 {

    private final IReporteService reporteService;
    private final IExcelService excelService;
    private final IAWSS3Service awss3Service;
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final IToken token;
    private final FechaUtils fechaUtils;


    @Scheduled(fixedRate = 30000) // Ejecutar cada 5 minutos
    public void processColaSolicitudes() {
        Queue<CompletableFuture<ReporteResponse2Dto>> colaSolicitudes = reporteService.getColaSolicitudes();

        // Obtener todas las futuras solicitudes en la cola
        List<CompletableFuture<ReporteResponse2Dto>> futures = new ArrayList<>();
        while (!colaSolicitudes.isEmpty()) {
            CompletableFuture<ReporteResponse2Dto> future = colaSolicitudes.poll();
            if (future != null) {
                futures.add(future);
            }
        }

// Procesar todas las futuras solicitudes en paralelo
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

// Esperar a que todas las futuras solicitudes se completen
        allFutures.thenRun(() -> {
            // Procesar cada futura solicitud en paralelo
            futures.forEach(future -> {
                future.thenAccept(reporteResponseDto -> {
                    try {
                        CompletableFuture<ExcelResponseDto> excelResponseDtoCompletableFuture = excelService.generarExcelReporteBytes(
                                reporteResponseDto.getIndicadorVehiculoParqueadoPrimeraVez(),
                                reporteResponseDto.getIndicadorVehiculosMasVecesRegistradosDiferentesParqueaderos(),
                                reporteResponseDto.getIndicadorVehiculosMasVecesRegistrado(),
                                reporteResponseDto.getVehiculosPorCoincidencia(),
                                reporteResponseDto.getGananciasResponseDto(),
                                reporteResponseDto.getNombreParqueadero(),
                                reporteResponseDto.getPlaca()
                        );

                        if (excelResponseDtoCompletableFuture != null) {
                            excelResponseDtoCompletableFuture.thenAccept(excelResponse -> {
                                UploadFileResponseDto uploadFileResponseDto = awss3Service.uploadFileExcel(excelResponse.getArchivo());

                                Reporte reporte = new Reporte();
                                reporte.setArchivoNombre(uploadFileResponseDto.getNombreArchivo());
                                reporte.setFechaCreado(fechaUtils.convertirFechaUtcToColombia(awss3Service.getObjectUploadDate(uploadFileResponseDto.getNombreArchivo())));

                                String tokenJwt = reporteResponseDto.getTokenJwt();
                                Long usuarioId = token.getUsuarioAutenticadoId(tokenJwt);
                                Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();

                                reporte.setUsuario(usuario);
                                reporteRepository.save(reporte);

                                System.out.println("Reporte generado: " + reporteResponseDto);
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        });
    }
}


*/
