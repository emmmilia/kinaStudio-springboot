package com.kinastudio.webapp.dtos;

import java.math.BigDecimal;
import com.kinastudio.webapp.dominio.Cliente;

public class ClienteDto {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String notas;
    private BigDecimal saldoAFavor;

    public ClienteDto(Cliente cliente) {
        this.id = cliente.getId();
        this.nombreCompleto = cliente.getNombreCompleto();
        this.email = cliente.getEmail();
        this.telefono = cliente.getTelefono();
        this.notas = cliente.getNotas();
        this.saldoAFavor = cliente.getSaldoAFavor();
    }
    
    public Long getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNotas() {
        return notas;
    }

    public BigDecimal getSaldoAFavor() {
        return saldoAFavor;
    }
}
