package com.kinastudio.webapp.presentadores;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kinastudio.webapp.dominio.Categoria;
import com.kinastudio.webapp.dominio.Fachada;
import com.kinastudio.webapp.dominio.Material;
import com.kinastudio.webapp.dominio.excepciones.MaterialException;
import com.kinastudio.webapp.dtos.MaterialDto;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/studio/materiales")
public class PresentadorStudio {
    private Fachada fachada;

    public PresentadorStudio(Fachada fachada) {
        this.fachada = fachada;
    }

    @GetMapping
    public Commands listarMateriales() throws MaterialException {
        return Commands.create(new Command("listarMateriales", fachada.listarMateriales().stream().map(MaterialDto::new).collect(java.util.stream.Collectors.toList())));
    }

    @PostMapping("/crear")
    public Commands crearMaterial(@RequestParam String nombre, @RequestParam int stock, @RequestParam BigDecimal precioUnitario, @RequestParam String marca, @RequestParam Categoria categoria, @RequestParam int umbralMinimo) throws MaterialException {
        Material nuevo = fachada.crearMaterial(nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
        return Commands.create(new Command("nuevoMaterial", new MaterialDto(nuevo)));
    }

    @PutMapping("/{id}")
    public Commands modificarMaterial(@PathVariable long id, @RequestParam String nombre, @RequestParam int stock, @RequestParam BigDecimal precioUnitario, @RequestParam String marca, @RequestParam Categoria categoria, @RequestParam int umbralMinimo) throws MaterialException {
        Material entity = fachada.modificarMaterial(id, nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
        return Commands.create(new Command("modificarMaterial", new MaterialDto(entity)));
    }

    @DeleteMapping("/{id}")
    public Commands eliminarMaterial(@PathVariable long id) throws MaterialException {
        fachada.eliminarMaterial(id);
        return Commands.create(new Command("eliminarMaterial", id));
    }
}
