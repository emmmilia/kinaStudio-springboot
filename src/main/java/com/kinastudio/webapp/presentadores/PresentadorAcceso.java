package com.kinastudio.webapp.presentadores;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kinastudio.webapp.dominio.SistemaAcceso;

import com.kinastudio.webapp.dominio.Fachada;
import com.kinastudio.webapp.dominio.TipoUsuario;
import com.kinastudio.webapp.dominio.Usuario;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dtos.UsuarioDto;

import jakarta.servlet.http.HttpSession;

@RestController 
@RequestMapping ("/acceso")
public class PresentadorAcceso {
    private Fachada fachada;

    public PresentadorAcceso(Fachada fachada){
        this.fachada = fachada;
    }
    
    @PostMapping("/registroEsteticista")
    public Commands registrarEsteticista(@RequestParam String email, @RequestParam String nombreUsuario, @RequestParam String password, @RequestParam String nombreCompleto) 
     throws UsuarioException {
        Usuario usuarioNuevo = fachada.registrarEsteticista(email, nombreUsuario, password, nombreCompleto);
        return Commands.create(crearEsteticista(usuarioNuevo));
    }

    @PostMapping("/login")
    public Commands login(@RequestParam String nombreUsuario, @RequestParam String password, HttpSession session) throws UsuarioException {
        Usuario usuario = fachada.login(nombreUsuario, password);
        session.setAttribute(SistemaAcceso.ATRIBUTO_USUARIO, usuario);
        return Commands.create(new Command("login", new UsuarioDto(usuario)));
    }

    private Command crearEsteticista(Usuario usuario){
        return new Command("nuevoUsuario", new UsuarioDto(usuario));
    }
    
}
