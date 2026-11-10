package com.kinastudio.webapp.dominio;

import java.math.BigDecimal;

public class Cliente {
    private long id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private BigDecimal saldoAFavor;
    private String notas;

    public Cliente(long id, String nombreCompleto, String email, String telefono, BigDecimal saldoAFavor, String notas){
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.saldoAFavor = saldoAFavor;
        this.notas = notas;

    }
}
