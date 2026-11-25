package com.kinastudio.webapp.dtos;

import java.math.BigDecimal;

import com.kinastudio.webapp.dominio.Categoria;
import com.kinastudio.webapp.dominio.Material;

public class MaterialDto {
    private long id;
    private String nombre;
    private int stock;
    private BigDecimal precioUnitario;
    private String marca;
    private Categoria categoria;
    private int umbralMinimo;

    public MaterialDto() {
    }

    public MaterialDto(Material material) {
        this.id = material.getId();
        this.nombre = material.getNombre();
        this.stock = material.getStock();
        this.precioUnitario = material.getPrecioUnitario();
        this.marca = material.getMarca();
        this.categoria = material.getCategoria();
        this.umbralMinimo = material.getUmbralMinimo();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getUmbralMinimo() {
        return umbralMinimo;
    }

    public void setUmbralMinimo(int umbralMinimo) {
        this.umbralMinimo = umbralMinimo;
    }
}
