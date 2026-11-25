package com.kinastudio.webapp.dominio.repositorios;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kinastudio.webapp.dominio.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByTelefono(String telefono);
    ArrayList<Cliente> findByNombreCompletoContainingIgnoreCase(String nombre);
}