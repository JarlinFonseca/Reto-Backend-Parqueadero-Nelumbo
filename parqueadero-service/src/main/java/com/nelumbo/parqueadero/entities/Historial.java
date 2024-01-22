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
@Table(name = "historial")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Historial {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "pago_total")
    private Double pagoTotal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_salida")
    private Date fechaSalida;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parqueadero_vehiculo_id", nullable = false)
    private ParqueaderoVehiculo parqueaderoVehiculo;
}
