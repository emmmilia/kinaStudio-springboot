package com.kinastudio.webapp.presentadores;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kinastudio.webapp.dominio.Fachada;
import com.kinastudio.webapp.dominio.Usuario;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dtos.UsuarioDto;

/**
 * Endpoints de apoyo al login. No tocan el dominio: la sesión la maneja Spring Security.
 */
@RestController
@RequestMapping("/acceso")
public class PresentadorSesion {

    private Fachada fachada;

    public PresentadorSesion(Fachada fachada){
        this.fachada = fachada;
    }

    /** Token que el front manda en el header indicado en las peticiones POST, PUT y DELETE. */
    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("headerName", token.getHeaderName(), "token", token.getToken());
    }

    /** Usuario con sesión iniciada. Sin sesión responde 401. */
    @GetMapping("/yo")
    public Map<String, String> yo(Authentication authentication) {
        String rol = authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse("")
            .replace("ROLE_", "");
        return Map.of("nombreUsuario", authentication.getName(), "rol", rol);
    }

     @PostMapping("/registroEsteticista")
    public Commands registrarEsteticista(@RequestParam String email, @RequestParam String nombreUsuario, @RequestParam String password, @RequestParam String nombreCompleto) 
     throws UsuarioException {
        Usuario usuarioNuevo = fachada.registrarEsteticista(email, nombreUsuario, password, nombreCompleto);
        return Commands.create(crearEsteticista(usuarioNuevo));
    }

    private Command crearEsteticista(Usuario usuario){
        return new Command("nuevoUsuario", new UsuarioDto(usuario));
    }
}