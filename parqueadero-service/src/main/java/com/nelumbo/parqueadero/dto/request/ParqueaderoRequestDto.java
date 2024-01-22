package com.nelumbo.parqueadero.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Getter
@Setter
public class ParqueaderoRequestDto {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    @NotNull(message = "La cantidadVehiculosMaximo no puede ser nulo")
    @Min(value = 1, message = "La cantidad de vehiculos máximo debe ser mayor a cero")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Long cantidadVehiculosMaximo;
    @NotNull(message = "El costo por hora del vehículo no puede ser nulo")
    @Positive(message = "El costo por hora del vehículo debe ser mayor que cero")
    private Double costoHoraVehiculo;
    @NotNull(message = "El identificador del usuario(socio) no puede ser nulo")
    private Long usuarioId;
}
