package com.nelumbo.parqueadero.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "parqueaderos_vehiculos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParqueaderoVehiculo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;

    @Column(name = "flag_ingreso_activo")
    private Boolean flagIngresoActivo;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private Parqueadero parqueadnero;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;
}
