package com.kinastudio.webapp.dominio;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

import java.math.BigDecimal;

@Entity
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToMany
    @JoinTable(name = "servicio_esteticista", joinColumns = @JoinColumn(name = "servicio_id"), inverseJoinColumns = @JoinColumn(name = "esteticista_id"))
    private List<Usuario> esteticistasHabilitadas = new ArrayList<>();

    @Column(name = "tiempo_estimado", nullable = false)
    private int tiempoEstimado;

    @ManyToMany
    @JoinTable(name = "servicio_material", joinColumns = @JoinColumn(name = "servicio_id"), inverseJoinColumns = @JoinColumn(name = "material_id"))
    private List<Material> materiales = new ArrayList<>();

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    protected Servicio() {
    }

    public Servicio(String nombre, List<Usuario> esteticistasHabilitadas, int tiempoEstimado, List<Material> materiales, BigDecimal precioBase) {
        this.nombre = nombre;
        this.esteticistasHabilitadas = new ArrayList<>(esteticistasHabilitadas);
        this.tiempoEstimado = tiempoEstimado;
        this.materiales = new ArrayList<>(materiales);
        this.precioBase = precioBase;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Usuario> getEsteticistasHabilitadas() {
        return esteticistasHabilitadas;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public List<Material> getMateriales() {
        return materiales;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTiempoEstimado(int tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public void setEsteticistasHabilitadas(List<Usuario> esteticistasHabilitadas) {
    this.esteticistasHabilitadas = new ArrayList<>(esteticistasHabilitadas);
}

    public void setMateriales(List<Material> materiales) {
        this.materiales = new ArrayList<>(materiales);
}
}