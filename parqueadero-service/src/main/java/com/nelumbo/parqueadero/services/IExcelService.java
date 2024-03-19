package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.response.*;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IExcelService {
    void generarArchivoExcel(List<String> datos, String rutaArchivo) throws IOException;

    @Async("asyncExecutor")
    CompletableFuture<ExcelResponseDto> generarExcelReporte(List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez,
                                                            List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                                                            List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado,
                                                            List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia,
                                                            GananciasResponseDto indicadorGanancias,
                                                            String nombreParqueadero, String placa) throws IOException;

    @Async("asyncExecutor")
    CompletableFuture<ExcelResponseDto> generarExcelReporteBytes(List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez,
                                                            List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                                                            List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado,
                                                            List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia,
                                                            GananciasResponseDto indicadorGanancias,
                                                            String nombreParqueadero, String placa) throws IOException;


}
