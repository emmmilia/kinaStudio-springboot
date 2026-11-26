package com.kinastudio.webapp.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kinastudio.webapp.dominio.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    boolean existsByEmailIgnoreCase(String email);

    Usuario findByNombreUsuarioIgnoreCase(String nombreUsuario);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Usuario findByEmailIgnoreCase(String email);
}
