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
    private Usuario esteticista;
    private Servicio servicio;
    private BigDecimal precioFinal;
    //private Estado estado
    private Cliente cliente;
    private String nombreCliente;
    private String mailCliente;
    private LocalDateTime fechaVencimientoSenia;
    private LocalTime horaFin;

    public Turno(long id, LocalDate fecha, LocalTime hora, List<Pago> pagos, Usuario esteticista, Servicio servicio, BigDecimal precioFinal, Cliente cliente,
        String nombreCliente, String mailCliente, LocalDateTime fechaVencimientoSenia, LocalTime horaFin)
    {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.pagos = pagos;
        this.esteticista = esteticista;
        this.servicio = servicio;
        this.precioFinal = precioFinal;
        this.cliente = cliente;
        this.nombreCliente = nombreCliente;
        this.mailCliente = mailCliente;
        this.fechaVencimientoSenia = fechaVencimientoSenia;
        this.horaFin = horaFin;
    }
}
