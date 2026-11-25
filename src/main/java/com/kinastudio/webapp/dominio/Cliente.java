package com.kinastudio.webapp.dominio;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String telefono;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldoAFavor = BigDecimal.ZERO;

    @Column(length = 1000)
    private String notas;

    // @OneToMany(mappedBy = "cliente")
    // @JsonIgnore
    // private ArrayList<Turno> turnos = new ArrayList<>();

    public Cliente(){}

    public Cliente(String nombreCompleto, String email, String telefono, String notas){
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.notas = notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public BigDecimal getSaldoAFavor() {
        return saldoAFavor;
    }

    public void setSaldoAFavor(BigDecimal saldoAFavor) {
        this.saldoAFavor = saldoAFavor;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    // public ArrayList<Turno> getTurnos() {
    //     return turnos;
    // }
}
