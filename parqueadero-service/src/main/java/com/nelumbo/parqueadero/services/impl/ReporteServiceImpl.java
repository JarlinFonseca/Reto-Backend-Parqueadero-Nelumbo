package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.services.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
@Transactional
@Getter
public class ReporteServiceImpl implements IReporteService {
    private final IExcelService excelService;


    public final Queue<CompletableFuture<ReporteResponse2Dto>> colaSolicitudes = new ConcurrentLinkedQueue<>();

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<ReporteResponseDto> generarReporteIndicadores(List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez,
                                                                           List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                                                                           List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado,
                                                                           List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia,
                                                                           GananciasResponseDto indicadorGanancias,
                                                                           String nombreParqueadero, String placa) throws IOException {

        CompletableFuture<ExcelResponseDto>  excelFuture = excelService.generarExcelReporte(indicadorVehiculoParqueadoPrimeraVez,
                indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                indicadorVehiculosMasVecesRegistrado,
                vehiculosPorCoincidencia,
                indicadorGanancias,nombreParqueadero, placa);


        CompletableFuture<ReporteResponseDto> reporteFuture = excelFuture.thenApply(excelResponseDto -> {
            ReporteResponseDto reporteResponseDto = new ReporteResponseDto();
            reporteResponseDto.setMensaje("Reporte generado correctamente");
            reporteResponseDto.setExcelResponse(excelResponseDto);
            return reporteResponseDto;
        });
       // colaSolicitudes.add(reporteFuture);

        return reporteFuture;
    }

    @Override
    public CompletableFuture<ReporteResponse2Dto> generarReporteIndicadores2(List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez,
                                                                             List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                                                                             List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado,
                                                                             List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia,
                                                                             GananciasResponseDto indicadorGanancias,
                                                                             String nombreParqueadero, String placa) {
        ReporteResponse2Dto reporteResponse2Dto = new ReporteResponse2Dto();
        reporteResponse2Dto.setIndicadorVehiculoParqueadoPrimeraVez(indicadorVehiculoParqueadoPrimeraVez);
        reporteResponse2Dto.setIndicadorVehiculosMasVecesRegistrado(indicadorVehiculosMasVecesRegistrado);
        reporteResponse2Dto.setIndicadorVehiculosMasVecesRegistradosDiferentesParqueaderos(indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos);
        reporteResponse2Dto.setVehiculosPorCoincidencia(vehiculosPorCoincidencia);
        reporteResponse2Dto.setGananciasResponseDto(indicadorGanancias);
        reporteResponse2Dto.setNombreParqueadero(nombreParqueadero);
        reporteResponse2Dto.setPlaca(placa);


        CompletableFuture<ReporteResponse2Dto> reporteFuture = CompletableFuture.completedFuture(reporteResponse2Dto);
        colaSolicitudes.add(reporteFuture);

        return reporteFuture;
    }


}
