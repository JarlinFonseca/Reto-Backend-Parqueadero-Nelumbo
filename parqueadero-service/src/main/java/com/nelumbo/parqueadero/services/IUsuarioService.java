package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.request.UsuarioRequestDto;
import com.nelumbo.parqueadero.dto.response.UsuarioResponseDto;

public interface IUsuarioService {
    UsuarioResponseDto guadarSocio(UsuarioRequestDto usuarioRequestDto);

}
