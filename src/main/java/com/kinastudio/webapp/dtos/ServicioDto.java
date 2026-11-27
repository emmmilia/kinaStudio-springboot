package com.kinastudio.webapp.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.kinastudio.webapp.dominio.Servicio;

public record ServicioDto(
        long id,
        String nombre,
        int tiempoEstimado,
        BigDecimal precioBase,
        List<UsuarioDto> esteticistasHabilitadas,
        List<MaterialDto> materiales) {

    public ServicioDto(Servicio servicio) {
        this(servicio.getId(),
                servicio.getNombre(),
                servicio.getTiempoEstimado(),
                servicio.getPrecioBase(),
                servicio.getEsteticistasHabilitadas().stream().map(UsuarioDto::new).toList(),
                servicio.getMateriales().stream().map(MaterialDto::new).toList());
    }


    public String getNombre() {
        return nombre;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }


    }