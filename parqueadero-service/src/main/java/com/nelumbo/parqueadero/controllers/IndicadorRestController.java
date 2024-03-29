package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.dto.response.GananciasResponseDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoResponseDto;
import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.services.IHistorialService;
import com.nelumbo.parqueadero.services.IParqueaderoVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/indicadores")
@RequiredArgsConstructor
public class IndicadorRestController {

    private final IParqueaderoVehiculoService parqueaderoVehiculoService;
    private final IHistorialService historialService;

    @GetMapping("/parqueaderos/vehiculos/mas-veces-registrados")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto>> obtenerVehiculosMasVecesRegistradosDiferentesParqueaderos(){
        return ResponseEntity.ok(parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez());
    }

    @GetMapping("/parqueaderos/{id}/vehiculos/mas-veces-registrados")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<List<IndicadorVehiculosMasVecesRegistradoResponseDto>> obtenerVehiculosMasVecesRegistradosParqueaderoPorId(@PathVariable(name="id")Long parqueaderoId){
        return ResponseEntity.ok(parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosParqueaderoPorId(parqueaderoId));
    }

    @GetMapping("/parqueaderos/{id}/primera-vez")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<List<VehiculoParqueadoResponseDto>> obtenerVehiculosParqueadosPrimeraVezPorParqueaderoId(@PathVariable(name = "id")Long parqueaderoId){
        return ResponseEntity.ok(historialService.obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(parqueaderoId));
    }

    @GetMapping("/parqueaderos/{id}/ganancias")
    @PreAuthorize("hasAuthority('SOCIO')")
    public ResponseEntity<GananciasResponseDto> obtenerGanancias(@PathVariable(name="id")Long parqueaderoId){
        return ResponseEntity.ok(historialService.obtenerGanancias(parqueaderoId));
    }

    @GetMapping("/vehiculos/{placa}/coincidencias")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<List<VehiculoParqueadoResponseDto>> buscarVehiculoPorCoindicencia(@PathVariable(name = "placa") String placa){
        return ResponseEntity.ok(parqueaderoVehiculoService.buscarVehiculoPorCoincidenciaPlaca(placa));
    }
}
