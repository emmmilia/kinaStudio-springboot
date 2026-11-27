package com.kinastudio.webapp.presentadores;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kinastudio.webapp.dominio.Categoria;
import com.kinastudio.webapp.dominio.Fachada;
import com.kinastudio.webapp.dominio.Material;
import com.kinastudio.webapp.dominio.Servicio;
import com.kinastudio.webapp.dominio.Usuario;
import com.kinastudio.webapp.dominio.excepciones.MaterialException;
import com.kinastudio.webapp.dominio.excepciones.ServicioException;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dtos.MaterialDto;
import com.kinastudio.webapp.dtos.ServicioDto;
import com.kinastudio.webapp.dtos.UsuarioDto;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/studio")
public class PresentadorStudio {
    private Fachada fachada;
    private List<Usuario> esteticistas = new ArrayList<>();
    private List<Material> materiales = new ArrayList<>();

    public PresentadorStudio(Fachada fachada) {
        this.fachada = fachada;
    }

    //materiales

    @PostMapping("/vistaConectada")
    public Commands vistaConectada() throws MaterialException, UsuarioException {
        return Commands.create(Materiales(), Esteticistas());
    }
    
    @GetMapping
    public Commands listarMateriales() throws MaterialException {
        return Commands.create(new Command("listarMateriales", fachada.listarMateriales().stream().map(MaterialDto::new).collect(java.util.stream.Collectors.toList())));
    }

    @PostMapping("/materiales/crear")
    public Commands crearMaterial(@RequestParam String nombre, @RequestParam int stock, @RequestParam BigDecimal precioUnitario, @RequestParam String marca, @RequestParam Categoria categoria, @RequestParam int umbralMinimo) throws MaterialException {
        Material nuevo = fachada.crearMaterial(nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
        return Commands.create(new Command("nuevoMaterial", new MaterialDto(nuevo)));
    }

    @PutMapping("/materiales/{id}")
    public Commands modificarMaterial(@PathVariable long id, @RequestParam String nombre, @RequestParam int stock, @RequestParam BigDecimal precioUnitario, @RequestParam String marca, @RequestParam Categoria categoria, @RequestParam int umbralMinimo) throws MaterialException {
        Material entity = fachada.modificarMaterial(id, nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
        return Commands.create(new Command("modificarMaterial", new MaterialDto(entity)));
    }

    @DeleteMapping("/materiales/{id}")
    public Commands eliminarMaterial(@PathVariable long id) throws MaterialException {
        fachada.eliminarMaterial(id);
        return Commands.create(new Command("eliminarMaterial", id));
    }

    //servicios

    @PostMapping("/servicios/crear")
    public Commands crearServicio(@RequestParam String nombre, @RequestParam List<Usuario> esteticistasHabilitadas, @RequestParam int tiempoEstimado, @RequestParam List<Material> materiales, @RequestParam BigDecimal precioBase) throws ServicioException {
        fachada.crearServicio(nombre, esteticistasHabilitadas, tiempoEstimado, materiales, precioBase);
        return Commands.create(new Command("servicioCreado", null));
    }

    private Command Materiales() throws MaterialException{
        materiales = fachada.listarMateriales();
        return new Command("materiales", materiales.stream().map(MaterialDto::new).collect(java.util.stream.Collectors.toList()));
    }

    private Command Esteticistas() throws UsuarioException{
        esteticistas = fachada.listarEsteticistas();
        return new Command("esteticistas", esteticistas.stream().map(UsuarioDto::new).collect(java.util.stream.Collectors.toList()));
    }

    @PutMapping("/servicios/{id}")
    public Commands modificarServicio(@PathVariable long id, @RequestParam String nombre, @RequestParam List<Usuario> esteticistasHabilitadas, @RequestParam int tiempoEstimado, @RequestParam List<Material> materiales, @RequestParam BigDecimal precioBase) throws ServicioException {
        Servicio servicio = fachada.modificarServicio(id, nombre, esteticistasHabilitadas, tiempoEstimado, materiales, precioBase);
        return Commands.create(new Command("modificarServicio", new ServicioDto(servicio)));
    }

    @DeleteMapping("/servicios/{id}")
    public Commands eliminarServicio(@PathVariable long id) throws ServicioException {
        fachada.eliminarServicio(id);
        return Commands.create(new Command("eliminarServicio", id));
    }

    @GetMapping("/servicios")
    public Commands listarServicios() throws ServicioException {
        return Commands.create(new Command("listarServicios", fachada.listarServicios().stream().map(ServicioDto::new).collect(java.util.stream.Collectors.toList())));
    }
}
