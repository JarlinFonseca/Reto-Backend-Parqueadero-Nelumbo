package com.nelumbo.correo.controllers;

import com.nelumbo.correo.dtos.request.MensajeRequestDto;
import com.nelumbo.correo.dtos.response.HistorialCorreoResponseDto;
import com.nelumbo.correo.dtos.response.MensajeResponseDto;
import com.nelumbo.correo.services.IMensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/correo")
@RequiredArgsConstructor
public class MensajeRestController {

    private final IMensajeService mensajeService;


    @PostMapping("/enviar")
    private ResponseEntity<MensajeResponseDto> enviarCorreo(@Valid @RequestBody MensajeRequestDto mensajeRequestDto){
        return ResponseEntity.ok(mensajeService.enviarCorreo(mensajeRequestDto));
    }

    @GetMapping("/obtenerCorreos")
    private ResponseEntity<HistorialCorreoResponseDto> obtenerCorreos(){
        return ResponseEntity.ok(mensajeService.obtenerCorreosEnviados());
    }
}
