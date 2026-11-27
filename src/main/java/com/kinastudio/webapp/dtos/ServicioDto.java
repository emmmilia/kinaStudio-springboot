package com.kinastudio.webapp.dtos;

import java.util.ArrayList;
import java.util.List;

import com.kinastudio.webapp.dominio.Material;
import com.kinastudio.webapp.dominio.Servicio;
import com.kinastudio.webapp.dominio.Usuario;

public class ServicioDto {
    private String nombre;
    private int tiempoEstimado;
    private ArrayList<Material> materiales;
    private String precioBase;
    private List<Usuario> esteticistasHabilitadas;

    public ServicioDto(Servicio servicio) {
        this.nombre = servicio.getNombre();
        this.tiempoEstimado = servicio.getTiempoEstimado();
        this.materiales = new ArrayList<>(servicio.getMateriales());
        this.precioBase = servicio.getPrecioBase().toString();
        this.esteticistasHabilitadas = new ArrayList<>(servicio.getEsteticistasHabilitadas());
    }

    public String getNombre() {
        return nombre;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

}
