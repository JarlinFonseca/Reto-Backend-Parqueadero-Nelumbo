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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final IReporteService reporteService;
    private final IExcelService excelService;
    private final IAWSS3Service awss3Service;
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final IToken token;
    private final FechaUtils fechaUtils;
   private static final AtomicLong contador = new AtomicLong(0);

    @Scheduled(fixedRate = 30000) // Ejecutar cada 5 minutos
    public void processColaSolicitudes() {
        Queue<CompletableFuture<ReporteResponse2Dto>> colaSolicitudes = reporteService.getColaSolicitudes();
        Map<Long, CompletableFuture<ReporteResponse2Dto>> mapaSolicitudes = new HashMap<>();
       // AtomicLong contador = new AtomicLong(0);
        // Procesar hasta 2 solicitudes en paralelo
        for (int i = 0; i < 2; i++) {
            CompletableFuture<ReporteResponse2Dto> future = colaSolicitudes.poll();
            if (future != null) {
                //PROBAR
                long identificador = contador.getAndIncrement();
                mapaSolicitudes.put(identificador, future);

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
                               UploadFileResponseDto uploadFileResponseDto= awss3Service.uploadFileExcel(excelResponse.getArchivo());

                                Reporte reporte = new Reporte();
                                reporte.setArchivoNombre(uploadFileResponseDto.getNombreArchivo());
                                reporte.setFechaCreado(fechaUtils.convertirFechaUtcToColombia(awss3Service.getObjectUploadDate(uploadFileResponseDto.getNombreArchivo())));
                                reporte.setFechaGuardado(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                                reporte.setIncrementador(contador.getAndIncrement());


                                String tokenJwt = reporteResponseDto.getTokenJwt();
                                Long usuarioId = token.getUsuarioAutenticadoId(tokenJwt);
                                Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();

                                reporte.setUsuario(usuario);
                                reporteRepository.save(reporte);


                            });
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    System.out.println("Reporte generado: " + reporteResponseDto);
                });
            } else {
                break; // Si no hay más solicitudes en la cola, sal del bucle
            }
        }
        //PROBAR
        // Esperar a que todas las solicitudes se completen
        CompletableFuture<Void> todasLasSolicitudes = CompletableFuture.allOf(mapaSolicitudes.values().toArray(new CompletableFuture[0]));

        todasLasSolicitudes.thenRun(() -> {
            // Encontrar la última solicitud
            Long ultimoIdentificador = mapaSolicitudes.keySet().stream().max(Long::compare).orElse(null);
            CompletableFuture<ReporteResponse2Dto> ultimaSolicitud = mapaSolicitudes.get(ultimoIdentificador);
            // Realizar acciones adicionales con la última solicitud...
        });

    }
}



