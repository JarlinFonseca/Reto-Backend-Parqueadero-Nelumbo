package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.dto.request.ParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.ParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.ParqueaderoSaveResponseDto;
import com.nelumbo.parqueadero.dto.response.ParqueaderoSocioResponseDto;
import com.nelumbo.parqueadero.services.IParqueaderoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/parqueaderos")
@RequiredArgsConstructor
public class ParqueaderoRestController {

    private final IParqueaderoService parqueaderoService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParqueaderoSaveResponseDto> guardarParqueadero(@Valid @RequestBody ParqueaderoRequestDto parqueaderoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parqueaderoService.guardarParqueadero(parqueaderoRequestDto));
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ParqueaderoResponseDto>> listarParqueaderos(){
        return ResponseEntity.status(HttpStatus.OK).body(parqueaderoService.listarParqueaderos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParqueaderoResponseDto> obtenerParqueaderoPorId(@PathVariable(name = "id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(parqueaderoService.obtenerParqueaderoPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParqueaderoResponseDto> actualizarParqueadero(@PathVariable(name = "id")Long id, @Valid @RequestBody ParqueaderoRequestDto parqueaderoRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(parqueaderoService.actualizarParqueadero(id, parqueaderoRequestDto) );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void eliminarParqueadero(@PathVariable(name = "id")Long id){
        parqueaderoService.eliminarParqueadero(id);
    }

    @GetMapping("/socios")
    @PreAuthorize("hasAuthority('SOCIO')")
    public ResponseEntity<List<ParqueaderoSocioResponseDto>> listarParqueaderosSocios(){
        return ResponseEntity.status(HttpStatus.OK).body(parqueaderoService.listarParqueaderosSocio());
    }

}
