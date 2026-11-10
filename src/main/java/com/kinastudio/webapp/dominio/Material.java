package com.kinastudio.webapp.dominio;
import java.math.BigDecimal;

public class Material {
    private long id;
    private String nombre;
    private int stock;
    private BigDecimal precioUnitario;
    private String marca;
    private String categoria;
    private int umbralMinimo;

    public Material(long id, String nombre, int stock, BigDecimal precioUnitario, String marca, String categoria, int umbralMinimo){
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.precioUnitario = precioUnitario;
        this.marca = marca;
        this.categoria = categoria;
        this.umbralMinimo = umbralMinimo;
    }
}
