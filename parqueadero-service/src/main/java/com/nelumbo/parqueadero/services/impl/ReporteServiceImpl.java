package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.IndicadoresGeneralDto;
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
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public final Queue<CompletableFuture<ReporteResponseDto>> colaSolicitudes = new ConcurrentLinkedQueue<>();
    private final IExcelService excelService;
    private final IHistorialService historialService;
    private final IParqueaderoVehiculoService parqueaderoVehiculoService;
    private final IParqueaderoService parqueaderoService;
    private final ReporteRepository reporteRepository;
    private final IToken token;
    private final IAWSS3Service awss3Service;
    private final FechaUtils fechaUtils;


    @Override
    @Async("asyncExecutor")
    public CompletableFuture<ReporteResponseDto> generarReporteIndicadores(IndicadoresGeneralDto indicadoresGeneralDto) {
        ReporteResponseDto reporteResponseDto = new ReporteResponseDto();
        reporteResponseDto.setIndicadorVehiculoParqueadoPrimeraVez(indicadoresGeneralDto.getIndicadorVehiculoParqueadoPrimeraVez());
        reporteResponseDto.setIndicadorVehiculosMasVecesRegistrado(indicadoresGeneralDto.getIndicadorVehiculosMasVecesRegistrado());
        reporteResponseDto.setIndicadorVehiculosMasVecesRegistradosDiferentesParqueaderos(indicadoresGeneralDto.getIndicadorVehiculosMasVecesRegistradosDiferentesParqueaderos());
        reporteResponseDto.setVehiculosPorCoincidencia(indicadoresGeneralDto.getVehiculosPorCoincidencia());
        reporteResponseDto.setGananciasResponseDto(indicadoresGeneralDto.getIndicadorGanancias());
        reporteResponseDto.setNombreParqueadero(indicadoresGeneralDto.getNombreParqueadero());
        reporteResponseDto.setPlaca(indicadoresGeneralDto.getPlaca());
        reporteResponseDto.setTokenJwt(indicadoresGeneralDto.getTokenBearer());
        reporteResponseDto.setFechaSolicitud(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));


        CompletableFuture<ReporteResponseDto> reporteFuture = CompletableFuture.completedFuture(reporteResponseDto);
        colaSolicitudes.add(reporteFuture);

        return reporteFuture;
    }

    @Override
    public IndicadoresGeneralDto obtenerTodosIndicadores(Long parqueaderoId, String placa) {
        List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez = historialService.obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(parqueaderoId);
        List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez();
        List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosParqueaderoPorId(parqueaderoId);
        List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia = parqueaderoVehiculoService.buscarVehiculoPorCoincidenciaPlaca(placa);


        String tokenBearer = token.getBearerToken();

        String nombreParqueadero = parqueaderoService.obtenerParqueaderoPorId(parqueaderoId).getNombre();


        GananciasResponseDto gananciasResponseDto = null;
        if (Boolean.TRUE.equals(esRolSocio())) {
            gananciasResponseDto = historialService.obtenerGanancias(parqueaderoId);
        }

        IndicadoresGeneralDto indicadoresGeneralDto = new IndicadoresGeneralDto();
        indicadoresGeneralDto.setIndicadorVehiculoParqueadoPrimeraVez(indicadorVehiculoParqueadoPrimeraVez);
        indicadoresGeneralDto.setIndicadorVehiculosMasVecesRegistradosDiferentesParqueaderos(indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos);
        indicadoresGeneralDto.setIndicadorVehiculosMasVecesRegistrado(indicadorVehiculosMasVecesRegistrado);
        indicadoresGeneralDto.setVehiculosPorCoincidencia(vehiculosPorCoincidencia);
        indicadoresGeneralDto.setIndicadorGanancias(gananciasResponseDto);
        indicadoresGeneralDto.setNombreParqueadero(nombreParqueadero);
        indicadoresGeneralDto.setPlaca(placa);
        indicadoresGeneralDto.setTokenBearer(tokenBearer);

        return indicadoresGeneralDto;
    }

    @Override
    public InputStream descargarUltimoReporte() {
        return awss3Service.descargarArchivo();
    }

    @Override
    public FechaReporteResponseDto obtenerFechaUltimoReporte() {
        String tokenBearer = token.getBearerToken();
        Long usuarioId = token.getUsuarioAutenticadoId(tokenBearer);
        Date fecha = reporteRepository.findFechaGuardadoUltimoReportePorUsuarioId(usuarioId).orElseThrow();
        FechaReporteResponseDto fechaReporteResponseDto = new FechaReporteResponseDto();
        fechaReporteResponseDto.setFecha(fechaUtils.convertirFechaToString(fecha));
        return fechaReporteResponseDto;
    }

    private Boolean esRolSocio() {
        String tokenBearer = token.getBearerToken();
        if (tokenBearer == null) throw new UsuarioSocioNoAutenticadoException();
        String rolSocioAuth = token.getUsuarioAutenticadoRol(tokenBearer);
        return rolSocioAuth.equals("SOCIO");
    }

}
