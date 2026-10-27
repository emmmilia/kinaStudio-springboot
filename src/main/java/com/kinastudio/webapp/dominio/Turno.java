package com.kinastudio.webapp.dominio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Turno {
    private long id;
    private LocalDate fecha;
    private LocalTime hora;
    private List<Pago> pagos;
    private Esteticista esteticista;
    private Servicio servicio;
    private BigDecimal precioFinal;
    //private Estado estado
    private Cliente cliente;
    private String nombreCliente;
    private String mailCliente;
    private LocalDateTime fechaVencimientoSenia;
    private LocalTime horaFin;

    public Turno(){
        
    }
}
