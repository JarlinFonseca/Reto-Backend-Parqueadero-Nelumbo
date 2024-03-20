package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.IndicadoresGeneralDto;
import com.nelumbo.parqueadero.dto.response.FechaReporteResponseDto;
import com.nelumbo.parqueadero.dto.response.ReporteResponseDto;
import org.springframework.scheduling.annotation.Async;

import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;


public interface IReporteService {

    @Async("asyncExecutor")
    CompletableFuture<ReporteResponseDto> generarReporteIndicadores(IndicadoresGeneralDto indicadoresGeneralDto);

    Queue<CompletableFuture<ReporteResponseDto>> getColaSolicitudes();

    IndicadoresGeneralDto obtenerTodosIndicadores(Long parqueaderoId, String placa);


    InputStream descargarUltimoReporte();

    FechaReporteResponseDto obtenerFechaUltimoReporte();

}
