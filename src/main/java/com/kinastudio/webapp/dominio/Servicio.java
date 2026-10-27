package com.kinastudio.webapp.dominio;

import java.util.ArrayList;
import java.math.BigDecimal;

public class Servicio {
    private long id;
    private String nombre;
    private ArrayList<Esteticista> esteticistasHabilitadas;
    private int tiempoEstimado;
    private Comision comision;
    private ArrayList<Material> materiales;
    private BigDecimal precioBase;

    public Servicio(){
        
    }
}
