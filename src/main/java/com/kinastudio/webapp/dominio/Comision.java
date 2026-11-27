package com.kinastudio.webapp.dominio;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "comision")
public class Comision {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column (name = "porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column (name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column (name = "tipo_comision", nullable = false, length = 20)
    private String tipoComision; //(porcentaje o monto fijo)

    @Transient
    private Usuario esteticista;

    @Transient
    private Servicio servicio;

}
