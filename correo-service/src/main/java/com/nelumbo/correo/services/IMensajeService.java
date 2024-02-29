package com.nelumbo.correo.services;

import com.nelumbo.correo.dtos.request.MensajeRequestDto;
import com.nelumbo.correo.dtos.response.HistorialCorreoResponseDto;
import com.nelumbo.correo.dtos.response.MensajeResponseDto;


import java.time.LocalDateTime;


public interface IMensajeService {

    MensajeResponseDto enviarCorreo(MensajeRequestDto mensajeRequestDto);

    HistorialCorreoResponseDto obtenerCorreosFiltrados(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
