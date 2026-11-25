package com.kinastudio.webapp.presentadores;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kinastudio.webapp.dominio.Cliente;
import com.kinastudio.webapp.dominio.Fachada;
import com.kinastudio.webapp.dominio.excepciones.ClienteException;
import com.kinastudio.webapp.dtos.ClienteDto;

@RestController
@RequestMapping("/clientes")
public class PresentadorCliente {

    private Fachada fachada;

    public PresentadorCliente(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping("/crear")
    public Commands crearCliente(@RequestParam String nombreCompleto, @RequestParam(required = false) String email, @RequestParam String telefono, @RequestParam(required = false) String notas) throws ClienteException {
        Cliente nuevo = fachada.crearCliente(nombreCompleto, email, telefono, notas);
        return Commands.create(new Command("nuevoCliente", new ClienteDto(nuevo)));
    }

    @GetMapping
    public List<Cliente> listarClientes() throws ClienteException {
        return fachada.listarClientes();
    }

    @PutMapping("/{id}")
    public ClienteDto modificarCliente(@PathVariable Long id, @RequestParam String nombreCompleto, @RequestParam(required = false) String email, @RequestParam String telefono, @RequestParam(required = false) String notas) throws ClienteException {
        Cliente modificado = fachada.modificarCliente(id, nombreCompleto, email, telefono, notas);
        return new ClienteDto(modificado);
    }

    @DeleteMapping("/{id}")
    public void eliminarCliente(@PathVariable Long id) throws ClienteException {
        fachada.eliminarCliente(id);
    }
}