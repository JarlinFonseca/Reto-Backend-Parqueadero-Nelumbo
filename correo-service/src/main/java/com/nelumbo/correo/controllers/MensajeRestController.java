package com.nelumbo.correo.controllers;

import com.nelumbo.correo.dtos.request.MensajeRequestDto;
import com.nelumbo.correo.dtos.response.HistorialCorreoResponseDto;
import com.nelumbo.correo.dtos.response.MensajeResponseDto;
import com.nelumbo.correo.services.IMensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/correos")
@RequiredArgsConstructor
public class MensajeRestController {

    private final IMensajeService mensajeService;


    @PostMapping
    public ResponseEntity<MensajeResponseDto> enviarCorreo(@RequestBody @Valid MensajeRequestDto mensajeRequestDto){
        return ResponseEntity.ok(mensajeService.enviarCorreo(mensajeRequestDto));
    }

    @GetMapping
    public ResponseEntity<HistorialCorreoResponseDto> obtenerCorreosFiltrados(@RequestParam(name = "fechaInicio", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime fechaInicio,
                                                                     @RequestParam(name = "fechaFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fechaFin){
        return ResponseEntity.ok(mensajeService.obtenerCorreosFiltrados(fechaInicio, fechaFin));
    }
}
