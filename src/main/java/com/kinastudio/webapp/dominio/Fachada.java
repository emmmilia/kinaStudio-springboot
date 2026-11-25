package com.kinastudio.webapp.dominio;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kinastudio.webapp.dominio.excepciones.ClienteException;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;

@Component
public class Fachada {

    private final SistemaStudio sistemaStudio;
    private final SistemaAcceso sistemaAcceso;
    private final SistemaAgenda sistemaAgenda;
    private final SistemaPago sistemaPago;

    public Fachada(SistemaStudio sistemaStudio, SistemaAcceso sistemaAcceso,
                   SistemaAgenda sistemaAgenda, SistemaPago sistemaPago) {
        this.sistemaStudio = sistemaStudio;
        this.sistemaAcceso = sistemaAcceso;
        this.sistemaAgenda = sistemaAgenda;
        this.sistemaPago = sistemaPago;
    }

    public Usuario modificarUsuario(String nombreUsuario, String email, String nombreCompleto, String comentario) throws UsuarioException{
        return sistemaAcceso.modificarUsuario(nombreUsuario, email, nombreCompleto, comentario);
    }

    public Usuario obtenerUsuario(String nombreUsuario) throws UsuarioException {
        return sistemaAcceso.obtenerUsuario(nombreUsuario);
    }

    public Usuario registrarEsteticista(String email, String nombreUsuario, String password,
                                        String nombreCompleto) throws UsuarioException {
        return sistemaAcceso.crearUsuario(email, nombreUsuario, password, nombreCompleto, TipoUsuario.ESTETICISTA);
    }

    public Usuario registrarAdministradorInicial(String email, String nombreUsuario, String password,
                                                 String nombreCompleto) throws UsuarioException {
        return sistemaAcceso.crearUsuario(email, nombreUsuario, password, nombreCompleto, TipoUsuario.ADMINISTRADOR);
    }
    
    public void cambiarPassword(String nombreUsuario, String passwordActual, String nuevaPassword) throws UsuarioException{
        sistemaAcceso.cambiarPassword(nombreUsuario, passwordActual, nuevaPassword);
    }

    public void solicitarRecuperacionPassword(String email) throws UsuarioException{
        sistemaAcceso.solicitarRecuperacionPassword(email);
    }

    public void restablecerPassword(String token, String password) throws UsuarioException{
        sistemaAcceso.restablecerPassword(token, password);
    }

    public Cliente crearCliente(String nombreCompleto, String email, String telefono, String notas) throws ClienteException {
        return sistemaStudio.crearCliente(nombreCompleto, email, telefono, notas);
    }

    public List<Cliente> listarClientes() throws ClienteException {
        return sistemaStudio.listarClientes();
    }

    public Cliente modificarCliente(Long id, Cliente datos) throws ClienteException {
        return sistemaStudio.modificarCliente(id, datos);
    }

    public void eliminarCliente(Long id) throws ClienteException {
        sistemaStudio.eliminarCliente(id);
    }
}