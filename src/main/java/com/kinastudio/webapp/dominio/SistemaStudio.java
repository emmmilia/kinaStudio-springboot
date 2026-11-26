package com.kinastudio.webapp.dominio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kinastudio.webapp.dominio.excepciones.ClienteException;
import com.kinastudio.webapp.dominio.excepciones.MaterialException;
import com.kinastudio.webapp.dominio.repositorios.ClienteRepository;
import com.kinastudio.webapp.dominio.repositorios.MaterialRepository;

@Service
public class SistemaStudio {

    private final ClienteRepository clienteRepository;
    private final MaterialRepository materialRepository;

    public SistemaStudio(ClienteRepository clienteRepository, MaterialRepository materialRepository) {
        this.clienteRepository = clienteRepository;
        this.materialRepository = materialRepository;
    }

    public Cliente crearCliente(String nombreCompleto, String email, String telefono, String notas) throws ClienteException {      
        validarCliente(nombreCompleto, email, telefono);

        Cliente nuevo = new Cliente(nombreCompleto.trim(), email, telefono.trim(), notas);
        return clienteRepository.save(nuevo);
    }

    public List<Cliente> listarClientes() throws ClienteException {
        return clienteRepository.findAll();
    }



    public Cliente modificarCliente(Long id, String nombreCompleto, String email, String telefono, String notas) throws ClienteException {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteException("Cliente no encontrado"));
        validarCliente(nombreCompleto, email, telefono);
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

    public Material crearMaterial(String nombre, int stock, BigDecimal precioUnitario, String marca, Categoria categoria, int umbralMinimo) throws MaterialException {
        validarMaterial(nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
        if (materialRepository.existsByNombreIgnoreCaseAndMarcaIgnoreCase(nombre, marca)) {
            throw new MaterialException("Ya existe un material con ese nombre y marca.");
        }

        Material nuevo = new Material(nombre.trim(), stock, precioUnitario, marca.trim(), categoria, umbralMinimo);
        return materialRepository.save(nuevo);
    }

    public List<Material> listarMateriales() throws MaterialException {
        return materialRepository.findAll();
    }

    public Material modificarMaterial(long id, String nombre, int stock, BigDecimal precioUnitario, String marca, Categoria categoria, int umbralMinimo) throws MaterialException {
        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new MaterialException("Material no encontrado"));
        
        validarMaterial(nombre, stock, precioUnitario, marca, categoria, umbralMinimo);
        if (materialRepository.existsByNombreIgnoreCaseAndMarcaIgnoreCaseAndIdNot(nombre, marca, id)) {
            throw new MaterialException("Ya existe un material con ese nombre y marca.");
        }    
        material.setNombre(nombre);
        material.setStock(stock);
        material.setPrecioUnitario(precioUnitario);
        material.setMarca(marca);
        material.setCategoria(categoria);
        material.setUmbralMinimo(umbralMinimo);
        return materialRepository.save(material);
    }

    public void eliminarMaterial(Long id) throws MaterialException {
        if (!materialRepository.existsById(id)) {
            throw new MaterialException("Material no encontrado");
        }
        materialRepository.deleteById(id);
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private void validarCliente(String nombreCompleto, String email, String telefono) throws ClienteException {
        if (esVacio(nombreCompleto)) {
            throw new ClienteException("El nombre completo es obligatorio.");
        }
        if (esVacio(email)) {
            throw new ClienteException("El email es obligatorio.");
        }
        if (!ValidadorEmail.esValido(email)) {
            throw new ClienteException("El email no es válido.");
        }
        if (esVacio(telefono)) {
            throw new ClienteException("El teléfono es obligatorio.");
        }
    }

    private void validarMaterial(String nombre, int stock, BigDecimal precioUnitario, String marca, Categoria categoria, int umbralMinimo) throws MaterialException {
        if (esVacio(nombre)) {
            throw new MaterialException("El nombre es obligatorio.");
        }
        if (stock < 0) {
            throw new MaterialException("El stock no puede ser negativo.");
        }
        if (precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new MaterialException("El precio unitario no puede ser negativo.");
        }
        if (esVacio(marca)) {
            throw new MaterialException("La marca es obligatoria.");
        }
        if (categoria == null) {
            throw new MaterialException("La categoría es obligatoria.");
        }
        if (umbralMinimo < 0) {
            throw new MaterialException("El umbral mínimo no puede ser negativo.");
        }
    }
}