package com.nelumbo.correo.services.impl;

import com.nelumbo.correo.dtos.request.MensajeRequestDto;
import com.nelumbo.correo.dtos.response.HistorialCorreoResponseDto;
import com.nelumbo.correo.dtos.response.MensajeResponseDto;
import com.nelumbo.correo.entities.Mensaje;
import com.nelumbo.correo.exception.NoDataFoundException;
import com.nelumbo.correo.repositories.IMensajeRepository;
import com.nelumbo.correo.services.IMensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MensajeServiceImpl implements IMensajeService {
    private final IMensajeRepository mensajeRepository;
    @Override
    public MensajeResponseDto enviarCorreo(MensajeRequestDto mensajeRequestDto) {
        guardarDatos(mensajeRequestDto);
        MensajeResponseDto mensajeResponseDto = new MensajeResponseDto();
        mensajeResponseDto.setMensaje("Correo Enviado");
        return mensajeResponseDto;
    }

    @Override
    public HistorialCorreoResponseDto obtenerCorreosEnviados() {
        List<Mensaje> mensajeList = mensajeRepository.findAll();
        if(mensajeList.isEmpty()) throw new NoDataFoundException("No hay correos enviados");
        Long cantidadCorreosEnviados = (long) mensajeList.size();
        ZoneId zonaColombia = ZoneId.of("America/Bogota");

        List<Mensaje> mensajesColombia = mensajeList.stream().peek(mensaje->{
                    Date fechaEnvioUTC = mensaje.getFechaEnviado();
                    LocalDateTime fechaEnvioColombia = fechaEnvioUTC.toInstant().atZone(zonaColombia).toLocalDateTime();
                    mensaje.setFechaEnviado( Date.from(fechaEnvioColombia.atZone(ZoneId.systemDefault()).toInstant()));
                })
                .collect(Collectors.toList());
        return new HistorialCorreoResponseDto(mensajesColombia, cantidadCorreosEnviados);
    }

    private void guardarDatos(MensajeRequestDto mensajeRequestDto){
        Mensaje mensaje = new Mensaje();
        mensaje.setDescripcion(mensajeRequestDto.getDescripcion());
        mensaje.setEmail(mensajeRequestDto.getEmail());
        mensaje.setPlaca(mensajeRequestDto.getPlaca());
        mensaje.setParqueaderoNombre(mensajeRequestDto.getParqueaderoNombre());
        mensaje.setFechaEnviado(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        mensajeRepository.save(mensaje);
    }

}
