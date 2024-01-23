package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoResponseDto;
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
@RequestMapping("api/v1/indicador")
@RequiredArgsConstructor
public class IndicadorRestController {
    private final IParqueaderoVehiculoService parqueaderoVehiculoService;
    private final IHistorialService historialService;

    @GetMapping("/vehiculosMasVecesRegistradosParqueaderos")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<List<IndicadorVehiculosMasVecesRegistradoResponseDto>> obtenerVehiculosMasVecesRegistradosDiferentesParqueaderos(){
        return ResponseEntity.ok(parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez());
    }

    @GetMapping("/vehiculosMasVecesRegistrados/parqueadero/{id}")
    @PreAuthorize("hasAuthority('SOCIO') OR hasAuthority('ADMIN')")
    public ResponseEntity<List<IndicadorVehiculosMasVecesRegistradoResponseDto>> obtenerVehiculosMasVecesRegistradosParqueaderoPorId(@PathVariable(name="id")Long parqueaderoId){
        return ResponseEntity.ok(parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosParqueaderoPorId(parqueaderoId));
    }
}
