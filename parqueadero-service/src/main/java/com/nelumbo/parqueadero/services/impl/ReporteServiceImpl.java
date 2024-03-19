package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.exception.UsuarioSocioNoAutenticadoException;
import com.nelumbo.parqueadero.repositories.ReporteRepository;
import com.nelumbo.parqueadero.services.*;
import com.nelumbo.parqueadero.utils.FechaUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    private final IHistorialService historialService;
    private final IParqueaderoVehiculoService parqueaderoVehiculoService;
    private final IParqueaderoService parqueaderoService;
    private final ReporteRepository reporteRepository;
    private final IToken token;
    private final IAWSS3Service awss3Service;
    private final FechaUtils fechaUtils;

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<ReporteResponseDto> generarReporteIndicadores(Long parqueaderoId, String placa) throws IOException {
        List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez = historialService.obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(parqueaderoId);
        List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez();
        List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosParqueaderoPorId(parqueaderoId);
        List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia= parqueaderoVehiculoService.buscarVehiculoPorCoincidenciaPlaca(placa);

        String nombreParqueadero = parqueaderoService.obtenerParqueaderoPorId(parqueaderoId).getNombre();

        GananciasResponseDto gananciasResponseDto =null;
        if(Boolean.TRUE.equals(esRolSocio())){
            gananciasResponseDto = historialService.obtenerGanancias(parqueaderoId);
        }

        CompletableFuture<ExcelResponseDto>  excelFuture = excelService.generarExcelReporte(indicadorVehiculoParqueadoPrimeraVez,
                indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                indicadorVehiculosMasVecesRegistrado,
                vehiculosPorCoincidencia,
                gananciasResponseDto,nombreParqueadero, placa);


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
                                                                             String nombreParqueadero, String placa,  String tokenJwt) {
        ReporteResponse2Dto reporteResponse2Dto = new ReporteResponse2Dto();
        reporteResponse2Dto.setIndicadorVehiculoParqueadoPrimeraVez(indicadorVehiculoParqueadoPrimeraVez);
        reporteResponse2Dto.setIndicadorVehiculosMasVecesRegistrado(indicadorVehiculosMasVecesRegistrado);
        reporteResponse2Dto.setIndicadorVehiculosMasVecesRegistradosDiferentesParqueaderos(indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos);
        reporteResponse2Dto.setVehiculosPorCoincidencia(vehiculosPorCoincidencia);
        reporteResponse2Dto.setGananciasResponseDto(indicadorGanancias);
        reporteResponse2Dto.setNombreParqueadero(nombreParqueadero);
        reporteResponse2Dto.setPlaca(placa);
        reporteResponse2Dto.setTokenJwt(tokenJwt);


        CompletableFuture<ReporteResponse2Dto> reporteFuture = CompletableFuture.completedFuture(reporteResponse2Dto);
        colaSolicitudes.add(reporteFuture);

        return reporteFuture;
    }

    @Override
    public InputStream descargarUltimoReporte() {
        return awss3Service.descargarArchivo();
    }

    @Override
    public FechaReporteResponseDto obtenerFechaUltimoReporte() {
        String tokenBearer = token.getBearerToken();
      //  if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        Long usuarioId = token.getUsuarioAutenticadoId(tokenBearer);
        Date fecha = reporteRepository.findFechaGuardadoUltimoReportePorUsuarioId(usuarioId).orElseThrow();
         FechaReporteResponseDto fechaReporteResponseDto = new FechaReporteResponseDto();
         fechaReporteResponseDto.setFecha( fechaUtils.convertirFechaToString(fecha));
        return fechaReporteResponseDto;
    }

    public Boolean esRolSocio() {
        String tokenBearer = token.getBearerToken();
        if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        String rolSocioAuth = token.getUsuarioAutenticadoRol(tokenBearer);
        return rolSocioAuth.equals("SOCIO");
    }


}
