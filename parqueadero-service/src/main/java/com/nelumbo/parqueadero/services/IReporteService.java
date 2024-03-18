package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.response.*;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;


public interface IReporteService {

    @Async("asyncExecutor")
    CompletableFuture<ReporteResponseDto> generarReporteIndicadores(List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez,
                                                                    List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                                                                    List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado,
                                                                    List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia,
                                                                    GananciasResponseDto indicadorGanancias,
                                                                    String nombreParqueadero, String placa) throws IOException;


    @Async("asyncExecutor")
    CompletableFuture<ReporteResponse2Dto> generarReporteIndicadores2(List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez,
                                                                      List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                                                                      List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado,
                                                                      List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia,
                                                                      GananciasResponseDto indicadorGanancias,
                                                                      String nombreParqueadero, String placa);
     Queue<CompletableFuture<ReporteResponse2Dto>> getColaSolicitudes();

    //@Async("asyncExecutor")
   // Queue<CompletableFuture<ReporteResponseDto>> getColaSolicitudes();
}
