package com.kinastudio.webapp.config;

import java.util.Locale;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kinastudio.webapp.dominio.Usuario;
import com.kinastudio.webapp.dominio.repositorios.UsuarioRepository;

/**
 * Le dice a Spring Security cómo buscar un usuario al iniciar sesión.
 * El rol queda como ROLE_ADMINISTRADOR o ROLE_ESTETICISTA.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if(nombreUsuario.isEmpty()){
            new UsernameNotFoundException("Usuario o contraseña incorrectos.");
        }
        //   .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos."));

        return User.withUsername(usuario.getNombreUsuario())
            .password(usuario.getPasswordHash())
            .roles(usuario.getRol().name().toUpperCase(Locale.ROOT))
            .build();
    }
}