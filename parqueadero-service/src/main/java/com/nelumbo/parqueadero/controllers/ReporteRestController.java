package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.exception.UsuarioSocioNoAutenticadoException;
import com.nelumbo.parqueadero.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteRestController {

    private final IReporteService reporteService;
    private final IHistorialService historialService;
    private final IParqueaderoVehiculoService parqueaderoVehiculoService;
    private final IParqueaderoService parqueaderoService;
    private final IToken token;

    @GetMapping("/generar/parqueaderos/{id}/vehiculos/placa/{placa}")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<ReporteResponseAsyncDto> generarReporteAsync(@PathVariable(name="id")Long parqueaderoId, @PathVariable(name="placa")String placa)  {
        try {
            List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez = historialService.obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(parqueaderoId);
            List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez();
            List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosParqueaderoPorId(parqueaderoId);
            List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia= parqueaderoVehiculoService.buscarVehiculoPorCoincidenciaPlaca(placa);

            String nombreParqueadero = parqueaderoService.obtenerParqueaderoPorId(parqueaderoId).getNombre();

            GananciasResponseDto gananciasResponseDto =null;
            if(Boolean.TRUE.equals(esRolSocio())){
                gananciasResponseDto = historialService.obtenerGanancias(parqueaderoId);
            }


            reporteService.generarReporteIndicadores(indicadorVehiculoParqueadoPrimeraVez,
                    indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                    indicadorVehiculosMasVecesRegistrado, vehiculosPorCoincidencia,
                    gananciasResponseDto, nombreParqueadero, placa);
            return ResponseEntity.accepted().body(new ReporteResponseAsyncDto("La solicitud de reporte se ha recibido y se está procesando en segundo plano."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ReporteResponseAsyncDto("Error al procesar la solicitud de reporte."));
        }

    }

    @GetMapping("/generar2/parqueaderos/{id}/vehiculos/placa/{placa}")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<ReporteResponseAsyncDto> generarReporteAsync2(@PathVariable(name="id")Long parqueaderoId, @PathVariable(name="placa")String placa)  {
        List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez = historialService.obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(parqueaderoId);
        List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez();
        List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosParqueaderoPorId(parqueaderoId);
        List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia= parqueaderoVehiculoService.buscarVehiculoPorCoincidenciaPlaca(placa);

        String nombreParqueadero = parqueaderoService.obtenerParqueaderoPorId(parqueaderoId).getNombre();

        GananciasResponseDto gananciasResponseDto =null;
        if(Boolean.TRUE.equals(esRolSocio())){
            gananciasResponseDto = historialService.obtenerGanancias(parqueaderoId);
        }
            reporteService.generarReporteIndicadores2(indicadorVehiculoParqueadoPrimeraVez,
                    indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos,
                    indicadorVehiculosMasVecesRegistrado, vehiculosPorCoincidencia,
                    gananciasResponseDto, nombreParqueadero, placa);
            return ResponseEntity.accepted().body(new ReporteResponseAsyncDto("La solicitud de reporte se ha recibido y se está procesando en segundo plano."));


    }

    public Boolean esRolSocio() {
        String tokenBearer = token.getBearerToken();
        if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        String rolSocioAuth = token.getUsuarioAutenticadoRol(tokenBearer);
        return rolSocioAuth.equals("SOCIO");
    }
}
