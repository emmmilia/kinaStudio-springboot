package com.kinastudio.webapp.dominio;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "materiales")
public class Material {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false, length = 100)
    private String nombre;

    @Column (nullable = false)
    private int stock;

    @Column (name = "precio_unitario", nullable = false, precision = 10, scale = 2) 
    private BigDecimal precioUnitario;

    @Column (nullable = false, length = 100)
    private String marca;

    @Column (nullable = false, length = 100)
    private Categoria categoria;

    @Column (nullable = false)
    private int umbralMinimo;

    protected Material() {
    }

    public Material(String nombre, int stock, BigDecimal precioUnitario, String marca, Categoria categoria, int umbralMinimo){
        this.nombre = nombre;
        this.stock = stock;
        this.precioUnitario = precioUnitario;
        this.marca = marca;
        this.categoria = categoria;
        this.umbralMinimo = umbralMinimo;
    }

    public long getId() {
        return id;
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
