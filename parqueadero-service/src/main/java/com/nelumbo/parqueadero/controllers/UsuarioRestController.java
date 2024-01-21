package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.dto.request.UsuarioRequestDto;
import com.nelumbo.parqueadero.dto.response.UsuarioResponseDto;
import com.nelumbo.parqueadero.services.IUsuarioService;
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
@RequestMapping("api/v1/usuario")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final IUsuarioService usuarioService;

    @PostMapping("/agregarSocio")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UsuarioResponseDto> guardarSocio(@Valid @RequestBody UsuarioRequestDto usuarioRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guadarSocio(usuarioRequestDto));
    }
}
