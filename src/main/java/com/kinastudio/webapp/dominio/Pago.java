package com.kinastudio.webapp.dominio;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {
    private long id;
    private String metodoPago;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String tipo; //senia o saldo

    public Pago(long id, String metodoPago, BigDecimal monto, LocalDateTime fechaPago, String tipo){
        this.id = id;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.tipo = tipo;
    }
}
