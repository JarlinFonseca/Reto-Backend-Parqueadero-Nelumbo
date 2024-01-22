package com.nelumbo.parqueadero.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class IngresoVehiculoParqueaderoRequestDto {
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$", message = "La placa debe tener 6 caracteres de longitud, alfanumérica, no se permite caracteres especiales ni la letra ñ")
    private String placa;
    @NotNull(message = "El identificador del parqueadero no puede ser nulo")
    private Long parqueaderoId;
}
