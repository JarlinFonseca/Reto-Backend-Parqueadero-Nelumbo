package com.nelumbo.correo.entities;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Document(value = "mensajes")
@Data
public class Mensaje {

    @Id
    private String id;
    private String email;
    private String placa;
    private String descripcion;
    @Field(name = "parqueadero_nombre")
    private String parqueaderoNombre;
    private Date fechaEnviado;
}
