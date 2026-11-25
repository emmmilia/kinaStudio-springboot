package com.kinastudio.webapp.dominio;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kinastudio.webapp.dominio.excepciones.ClienteException;
import com.kinastudio.webapp.dominio.repositorios.ClienteRepository;

@Service
public class SistemaStudio {

    private final ClienteRepository clienteRepository;

    public SistemaStudio(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente crearCliente(String nombreCompleto, String email, String telefono, String notas)
            throws ClienteException {
        if (esVacio(nombreCompleto)) {
            throw new ClienteException("El nombre completo es obligatorio.");
        }
        if (esVacio(telefono)) {
            throw new ClienteException("El teléfono es obligatorio.");
        }
        Cliente nuevo = new Cliente(nombreCompleto.trim(), email, telefono.trim(), notas);
        return clienteRepository.save(nuevo);
    }

    public List<Cliente> listarClientes() throws ClienteException {
        return clienteRepository.findAll();
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public Cliente modificarCliente(Long id, String nombreCompleto, String email, String telefono, String notas) throws ClienteException {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteException("Cliente no encontrado"));
        cliente.setNombreCompleto(nombreCompleto);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setNotas(notas);
        return clienteRepository.save(cliente);
    }

    public void eliminarCliente(Long id) throws ClienteException {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteException("Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
    }
}