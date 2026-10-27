package com.kinastudio.webapp.dominio;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Comision {
    private long id;
    private BigDecimal monto;
    private Esteticista esteticista;
    private Servicio servicio;
    private String tipoComision; //(porcentaje o monto fijo)
    private LocalDate vigenteDesde;
}
