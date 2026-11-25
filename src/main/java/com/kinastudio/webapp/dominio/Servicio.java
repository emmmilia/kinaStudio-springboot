package com.kinastudio.webapp.dominio;

import java.util.ArrayList;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.persistence.GenerationType;

import java.math.BigDecimal;

public class Servicio {

    private long id;
    private String nombre;
    private ArrayList<Usuario> esteticistasHabilitadas;
    private int tiempoEstimado;
    private Comision comision;
    private ArrayList<Material> materiales;
    private BigDecimal precioBase;

    protected Servicio() {
    }

    public Servicio(long id, String nombre, ArrayList<Usuario> esteticistasHabilitadas, int tiempoEstimado, Comision comision, ArrayList<Material> materiales, BigDecimal precioBase) {
        this.id = id;
        this.nombre = nombre;
        this.esteticistasHabilitadas = esteticistasHabilitadas;
        this.tiempoEstimado = tiempoEstimado;
        this.comision = comision;
        this.materiales = materiales;
        this.precioBase = precioBase;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Usuario> getEsteticistasHabilitadas() {
        return esteticistasHabilitadas;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public Comision getComision() {
        return comision;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public ArrayList<Material> getMateriales() {
        return materiales;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEsteticistasHabilitadas(ArrayList<Usuario> esteticistasHabilitadas) {
        this.esteticistasHabilitadas = esteticistasHabilitadas;
    }

    public void setTiempoEstimado(int tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public void setComision(Comision comision) {
        this.comision = comision;
    }

    public void setMateriales(ArrayList<Material> materiales) {
        this.materiales = materiales;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }
}