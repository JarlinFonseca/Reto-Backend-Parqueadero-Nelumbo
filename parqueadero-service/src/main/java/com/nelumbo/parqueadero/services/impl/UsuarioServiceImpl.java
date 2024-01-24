package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.request.UsuarioRequestDto;
import com.nelumbo.parqueadero.dto.response.UsuarioResponseDto;
import com.nelumbo.parqueadero.entities.Usuario;
import com.nelumbo.parqueadero.mapper.IUsuarioRequestMapper;
import com.nelumbo.parqueadero.mapper.IUsuarioResponseMapper;
import com.nelumbo.parqueadero.repositories.UsuarioRepository;
import com.nelumbo.parqueadero.services.IRolService;
import com.nelumbo.parqueadero.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final IRolService rolService;
    private final IUsuarioRequestMapper usuarioRequestMapper;
    private final IUsuarioResponseMapper usuarioResponseMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDto guadarSocio(UsuarioRequestDto usuarioRequestDto) {
        Usuario usuario = usuarioRequestMapper.toUsuario(usuarioRequestDto);
        usuario.setClave(passwordEncoder.encode(usuarioRequestDto.getClave()));
        usuario.setFechaRegistro(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        usuario.setRol(rolService.obtenerRolPorId(2L));
        usuarioRepository.save(usuario);
        return usuarioResponseMapper.toResponse(usuario);
    }
}
