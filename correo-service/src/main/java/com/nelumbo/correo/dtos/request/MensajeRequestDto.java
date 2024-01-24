package com.nelumbo.correo.dtos.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MensajeRequestDto {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Ingrese un correo válido")
    private String email;
    @NotBlank(message = "La placa es requerida")
    private String placa;
    @NotBlank(message = "El mensaje es requerido")
    private String mensaje;
    @NotBlank(message = "El nombre del parqueadero es requerido")
    private String parqueaderoNombre;

}
