package com.kinastudio.webapp.dominio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kinastudio.webapp.dominio.excepciones.ClienteException;
import com.kinastudio.webapp.dominio.excepciones.MaterialException;
import com.kinastudio.webapp.dominio.excepciones.ServicioException;
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

    public List<Usuario> listarEsteticistas() throws UsuarioException {
        return sistemaAcceso.listarEsteticistas();
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

    public Cliente modificarCliente(Long id, String nombreCompleto, String email, String telefono, String notas) throws ClienteException {
        return sistemaStudio.modificarCliente(id, nombreCompleto, email, telefono, notas);
    }

    public void eliminarCliente(Long id) throws ClienteException {
        sistemaStudio.eliminarCliente(id);
    }

    public Material crearMaterial(String nombre, int stock, BigDecimal precioUnitario, String marca, Categoria categoria, int umbralMinimo) throws MaterialException {
        return sistemaStudio.crearMaterial(nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
    }

    public Material modificarMaterial(long id,String nombre, int stock, BigDecimal precioUnitario, String marca, Categoria categoria, int umbralMinimo) throws MaterialException {
        return sistemaStudio.modificarMaterial(id, nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
    }

    public void eliminarMaterial(Long id) throws MaterialException {
        sistemaStudio.eliminarMaterial(id);
    }

    public List<Material> listarMateriales() throws MaterialException {
        return sistemaStudio.listarMateriales();
    }

    public Servicio crearServicio(String nombre, List<Usuario> esteticistasHabilitadas, int tiempoEstimado, List<Material> materiales, BigDecimal precioBase) throws ServicioException {
        return sistemaStudio.crearServicio(nombre, esteticistasHabilitadas, tiempoEstimado, materiales, precioBase);
    }

    public Servicio modificarServicio(long id, String nombre, List<Usuario> esteticistasHabilitadas, int tiempoEstimado, List<Material> materiales, BigDecimal precioBase) throws ServicioException {
        return sistemaStudio.modificarServicio(id, nombre, esteticistasHabilitadas, tiempoEstimado, materiales, precioBase);
    }

    public void eliminarServicio(long id) throws ServicioException {
        sistemaStudio.eliminarServicio(id);
    }

    public List<Servicio> listarServicios() throws ServicioException {
        return sistemaStudio.listarServicios();
    }
}