package com.nelumbo.correo.services;

import com.nelumbo.correo.dtos.request.MensajeRequestDto;
import com.nelumbo.correo.dtos.response.HistorialCorreoResponseDto;
import com.nelumbo.correo.dtos.response.MensajeResponseDto;
import com.nelumbo.correo.entities.Mensaje;

public interface IMensajeService {

    MensajeResponseDto enviarCorreo(MensajeRequestDto mensajeRequestDto);

    HistorialCorreoResponseDto obtenerCorreosEnviados();
}
