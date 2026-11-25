package com.kinastudio.webapp.dominio.repositorios;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kinastudio.webapp.dominio.Categoria;
import com.kinastudio.webapp.dominio.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Material findByNombreIgnoreCase(String nombre);

    Material findByCategoria(Categoria categoria);

    boolean existsByNombreIgnoreCase(String nombre);

    ArrayList<Material> findByMarcaIgnoreCase(String marca);

    boolean existsByMarcaIgnoreCase(String marca);

    boolean existsByNombreIgnoreCaseAndMarcaIgnoreCase(String nombre, String marca);
}