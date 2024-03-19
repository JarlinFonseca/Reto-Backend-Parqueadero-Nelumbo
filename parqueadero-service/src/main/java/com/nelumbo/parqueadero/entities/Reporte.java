package com.nelumbo.parqueadero.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reportes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reporte {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "archivo_nombre")
    private String archivoNombre;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creado")
    private Date fechaCreado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_guardado")
    private Date fechaGuardado;

    private Long incrementador;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
