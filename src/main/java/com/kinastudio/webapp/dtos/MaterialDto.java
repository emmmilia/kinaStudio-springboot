package com.kinastudio.webapp.dtos;

import java.math.BigDecimal;

import com.kinastudio.webapp.dominio.Categoria;
import com.kinastudio.webapp.dominio.Material;

/**
 * Datos de un material que se mandan al front.
 * Es un record: Java arma solo el constructor, los getters, equals y toString,
 * así no se repiten los getters y setters de la entidad Material.
 */
public record MaterialDto(
        long id,
        String nombre,
        int stock,
        BigDecimal precioUnitario,
        String marca,
        Categoria categoria,
        int umbralMinimo) {

    public MaterialDto(Material material) {
        this(material.getId(),
                material.getNombre(),
                material.getStock(),
                material.getPrecioUnitario(),
                material.getMarca(),
                material.getCategoria(),
                material.getUmbralMinimo());
    }
}
