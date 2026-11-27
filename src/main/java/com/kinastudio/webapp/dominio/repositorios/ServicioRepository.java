package com.kinastudio.webapp.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kinastudio.webapp.dominio.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
}