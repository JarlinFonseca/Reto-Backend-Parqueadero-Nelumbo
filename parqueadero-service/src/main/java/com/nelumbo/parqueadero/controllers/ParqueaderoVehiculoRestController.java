package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.dto.request.IngresoVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.request.SalidaVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.IngresoVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.SalidaVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.services.IParqueaderoService;
import com.nelumbo.parqueadero.services.IParqueaderoVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/parqueaderoVehiculo")
@RequiredArgsConstructor
public class ParqueaderoVehiculoRestController {
    private final IParqueaderoVehiculoService parqueaderoVehiculoService;

    @PostMapping("/registrarIngreso")
    @PreAuthorize("hasAuthority('SOCIO')")
    public ResponseEntity<IngresoVehiculoParqueaderoResponseDto> registrarIngreso(@Valid @RequestBody IngresoVehiculoParqueaderoRequestDto ingresoVehiculoParqueaderoRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(parqueaderoVehiculoService.registrarIngreso(ingresoVehiculoParqueaderoRequestDto));
    }

    @PostMapping("/registrarSalida")
    @PreAuthorize("hasAuthority('SOCIO')")
    public ResponseEntity<SalidaVehiculoParqueaderoResponseDto> registrarSalida(@Valid @RequestBody SalidaVehiculoParqueaderoRequestDto salidaVehiculoParqueaderoRequestDto){
        return ResponseEntity.ok(parqueaderoVehiculoService.registrarSalida(salidaVehiculoParqueaderoRequestDto));
    }

}
