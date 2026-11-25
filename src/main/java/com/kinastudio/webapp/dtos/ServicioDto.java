package com.kinastudio.webapp.dtos;

import java.util.ArrayList;

import com.kinastudio.webapp.dominio.Material;

public class ServicioDto {
    private long id;
    private String nombre;
    private int tiempoEstimado;
    private String comision;
    private ArrayList<Material> materiales;
    private String precioBase;
    private String esteticistasHabilitadas;
    private String comentario;

    public ServicioDto(long id, String nombre, int tiempoEstimado, String comision, ArrayList<Material> materiales, String precioBase, String esteticistasHabilitadas, String comentario) {
        this.id = id;
        this.nombre = nombre;
        this.tiempoEstimado = tiempoEstimado;
        this.comision = comision;
        this.materiales = materiales;
        this.precioBase = precioBase;
        this.esteticistasHabilitadas = esteticistasHabilitadas;
        this.comentario = comentario;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public String getComision() {
        return comision;
    }
    
}
