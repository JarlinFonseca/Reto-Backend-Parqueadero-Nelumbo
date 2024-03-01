package com.nelumbo.correo.services.impl;

import com.nelumbo.correo.dtos.request.MensajeRequestDto;
import com.nelumbo.correo.dtos.response.HistorialCorreoResponseDto;
import com.nelumbo.correo.dtos.response.MensajeInfoResponseDto;
import com.nelumbo.correo.dtos.response.MensajeResponseDto;
import com.nelumbo.correo.entities.Mensaje;
import com.nelumbo.correo.repositories.IMensajeRepository;
import com.nelumbo.correo.services.IMensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public HistorialCorreoResponseDto obtenerCorreosFiltrados(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Mensaje> mensajeList;
        if(fechaInicio==null || fechaFin==null){
             mensajeList = mensajeRepository.findAll();
        }
        else{
            mensajeList =filtrarEntreFechas(fechaInicio, fechaFin);
        }

        Long cantidadCorreosEnviados = (long) mensajeList.size();
        List<MensajeInfoResponseDto> mensajesColombia = convertirFechaMensajeUTCToColombia(mensajeList);
        return new HistorialCorreoResponseDto(mensajesColombia, cantidadCorreosEnviados);
    }

    private  List<MensajeInfoResponseDto> convertirFechaMensajeUTCToColombia(List<Mensaje> mensajeList) {
        ZoneId zonaColombia = ZoneId.of("America/Bogota");
        return mensajeList.stream().map(mensaje -> setearDataMensaje(mensaje,zonaColombia)
        ).collect(Collectors.toList());
    }



    private void guardarDatos(MensajeRequestDto mensajeRequestDto) {
        Mensaje mensaje = new Mensaje();
        mensaje.setDescripcion(mensajeRequestDto.getDescripcion());
        mensaje.setEmail(mensajeRequestDto.getEmail());
        mensaje.setPlaca(mensajeRequestDto.getPlaca());
        mensaje.setParqueaderoNombre(mensajeRequestDto.getParqueaderoNombre());
        mensaje.setFechaEnviado(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        mensajeRepository.save(mensaje);
    }

    private MensajeInfoResponseDto setearDataMensaje(Mensaje mensaje, ZoneId zonaColombia){
        MensajeInfoResponseDto mensajeInfoResponseDto  = new MensajeInfoResponseDto();
        mensajeInfoResponseDto.setId(mensaje.getId());
        mensajeInfoResponseDto.setPlaca(mensaje.getPlaca());
        mensajeInfoResponseDto.setDescripcion(mensaje.getDescripcion());
        mensajeInfoResponseDto.setParqueaderoNombre(mensaje.getParqueaderoNombre());
        mensajeInfoResponseDto.setEmail(mensaje.getEmail());

        Date fechaEnvioUTC = mensaje.getFechaEnviado();
        LocalDateTime fechaEnvioColombia = fechaEnvioUTC.toInstant().atZone(zonaColombia).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaEnvio = fechaEnvioColombia.format(formatter);

        mensajeInfoResponseDto.setFechaEnviado(fechaEnvio);

        return mensajeInfoResponseDto;
    }

    private List<Mensaje> filtrarEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return mensajeRepository.findByFechaEnviadoBetween(fechaInicio, fechaFin);
    }


}
