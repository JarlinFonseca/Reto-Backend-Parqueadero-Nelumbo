package com.nelumbo.parqueadero.tasks;

import com.nelumbo.parqueadero.dto.response.ExcelResponseDto;
import com.nelumbo.parqueadero.dto.response.ReporteResponseDto;
import com.nelumbo.parqueadero.dto.response.UploadFileResponseDto;
import com.nelumbo.parqueadero.entities.Reporte;
import com.nelumbo.parqueadero.entities.Usuario;
import com.nelumbo.parqueadero.exception.ExcelErrorException;
import com.nelumbo.parqueadero.repositories.ReporteRepository;
import com.nelumbo.parqueadero.repositories.UsuarioRepository;
import com.nelumbo.parqueadero.services.IAWSS3Service;
import com.nelumbo.parqueadero.services.IExcelService;
import com.nelumbo.parqueadero.services.IReporteService;
import com.nelumbo.parqueadero.services.IToken;
import com.nelumbo.parqueadero.utils.FechaUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);
    private static final AtomicLong contador = new AtomicLong(0);

    @Scheduled(fixedRate = 300000) // Ejecutar cada 5 minutos
    public void processColaSolicitudes() {
        Queue<CompletableFuture<ReporteResponseDto>> colaSolicitudes = reporteService.getColaSolicitudes();
        // Procesar hasta 2 solicitudes en paralelo
        for (int i = 0; i < 2; i++) {
            CompletableFuture<ReporteResponseDto> future = colaSolicitudes.poll();
            if (future != null) {
                long identificador = contador.getAndIncrement();

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
                                reporte.setFechaSolicitud(reporteResponseDto.getFechaSolicitud());
                                reporte.setIncrementador(identificador);


                                String tokenJwt = reporteResponseDto.getTokenJwt();
                                Long usuarioId = token.getUsuarioAutenticadoId(tokenJwt);
                                Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();

                                reporte.setUsuario(usuario);
                                reporteRepository.save(reporte);


                            });
                        }

                    } catch (IOException e) {
                        throw new ExcelErrorException();
                    }

                    LOGGER.info("Reporte generado {}", reporteResponseDto);
                });
            } else {
                break; // Si no hay más solicitudes en la cola, sal del bucle
            }
        }
    }
}



