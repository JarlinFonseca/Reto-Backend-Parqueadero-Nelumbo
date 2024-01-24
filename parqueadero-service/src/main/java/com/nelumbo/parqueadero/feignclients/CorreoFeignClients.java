package com.nelumbo.parqueadero.feignclients;

import com.nelumbo.parqueadero.feignclients.dto.request.MensajeRequestDto;
import com.nelumbo.parqueadero.feignclients.dto.response.MensajeResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "correo-service", url="localhost:8082/api/v1/correo")
public interface CorreoFeignClients {

    @PostMapping("/enviar")
    ResponseEntity<MensajeResponseDto> enviarCorreo(@Valid @RequestBody MensajeRequestDto mensajeRequestDto);
}
