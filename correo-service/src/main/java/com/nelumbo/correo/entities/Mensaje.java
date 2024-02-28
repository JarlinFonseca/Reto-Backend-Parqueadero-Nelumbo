package com.nelumbo.correo.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mensajes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Mensaje {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String email;
    private String placa;
    private String descripcion;
    @Column(name = "parqueadero_nombre")
    private String parqueaderoNombre;

}
