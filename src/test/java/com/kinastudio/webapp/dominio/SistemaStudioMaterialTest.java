package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kinastudio.webapp.dominio.excepciones.MaterialException;
import com.kinastudio.webapp.dominio.repositorios.MaterialRepository;

// Pruebas del CRUD de materiales contra la base H2 en memoria.
// @Transactional deshace lo que guarda cada test, así no se afectan entre sí.
@SpringBootTest(properties = { "KINA_ADMIN_EMAIL=", "KINA_ADMIN_PASSWORD=" })
@Transactional
class SistemaStudioMaterialTest {

    private static final BigDecimal PRECIO = new BigDecimal("350.00");

    @Autowired
    private SistemaStudio sistema;

    @Autowired
    private MaterialRepository repositorio;

    private Material crearEsmalte() throws MaterialException {
        return sistema.crearMaterial("Esmalte rojo", 10, PRECIO, "OPI", Categoria.NAILS, 3);
    }

    // ---------- Crear ----------

    @Test
    void creaUnMaterialValido() throws MaterialException {
        Material m = crearEsmalte();

        assertTrue(m.getId() > 0);
        assertEquals("Esmalte rojo", m.getNombre());
        assertEquals(10, m.getStock());
        assertEquals(0, PRECIO.compareTo(m.getPrecioUnitario()));
        assertEquals("OPI", m.getMarca());
        assertEquals(Categoria.NAILS, m.getCategoria());
        assertEquals(3, m.getUmbralMinimo());
        assertEquals(1, repositorio.count());
    }

    @Test
    void sacaLosEspaciosDelNombreYLaMarca() throws MaterialException {
        Material m = sistema.crearMaterial("  Esmalte rojo  ", 10, PRECIO, "  OPI ", Categoria.NAILS, 3);

        assertEquals("Esmalte rojo", m.getNombre());
        assertEquals("OPI", m.getMarca());
    }

    @Test
    void aceptaStockPrecioYUmbralEnCero() {
        assertDoesNotThrow(() ->
            sistema.crearMaterial("Algodón", 0, BigDecimal.ZERO, "Genérica", Categoria.LIMPIEZA, 0));
    }

    @Test
    void rechazaNombreVacio() {
        for (String vacio : new String[] { null, "", "   " }) {
            MaterialException ex = assertThrows(MaterialException.class, () ->
                sistema.crearMaterial(vacio, 10, PRECIO, "OPI", Categoria.NAILS, 3));
            assertEquals("El nombre es obligatorio.", ex.getMessage());
        }
    }

    @Test
    void rechazaStockNegativo() {
        MaterialException ex = assertThrows(MaterialException.class, () ->
            sistema.crearMaterial("Esmalte rojo", -1, PRECIO, "OPI", Categoria.NAILS, 3));
        assertEquals("El stock no puede ser negativo.", ex.getMessage());
    }

    @Test
    void rechazaPrecioNegativo() {
        MaterialException ex = assertThrows(MaterialException.class, () ->
            sistema.crearMaterial("Esmalte rojo", 10, new BigDecimal("-1"), "OPI", Categoria.NAILS, 3));
        assertEquals("El precio unitario no puede ser negativo.", ex.getMessage());
    }

    @Test
    void rechazaMarcaVacia() {
        for (String vacia : new String[] { null, "", "   " }) {
            MaterialException ex = assertThrows(MaterialException.class, () ->
                sistema.crearMaterial("Esmalte rojo", 10, PRECIO, vacia, Categoria.NAILS, 3));
            assertEquals("La marca es obligatoria.", ex.getMessage());
        }
    }

    @Test
    void rechazaCategoriaNula() {
        MaterialException ex = assertThrows(MaterialException.class, () ->
            sistema.crearMaterial("Esmalte rojo", 10, PRECIO, "OPI", null, 3));
        assertEquals("La categoría es obligatoria.", ex.getMessage());
    }

    @Test
    void rechazaUmbralNegativo() {
        MaterialException ex = assertThrows(MaterialException.class, () ->
            sistema.crearMaterial("Esmalte rojo", 10, PRECIO, "OPI", Categoria.NAILS, -1));
        assertEquals("El umbral mínimo no puede ser negativo.", ex.getMessage());
    }

    @Test
    void rechazaMismoNombreYMarcaSinImportarMayusculas() throws MaterialException {
        crearEsmalte();

        MaterialException ex = assertThrows(MaterialException.class, () ->
            sistema.crearMaterial("ESMALTE ROJO", 5, PRECIO, "opi", Categoria.NAILS, 1));
        assertEquals("Ya existe un material con ese nombre y marca.", ex.getMessage());
    }

    @Test
    void unAltaRechazadaNoDejaNadaGuardado() {
        assertThrows(MaterialException.class, () ->
            sistema.crearMaterial("Esmalte rojo", -1, PRECIO, "OPI", Categoria.NAILS, 3));
        assertEquals(0, repositorio.count());
    }

    // ---------- Listar ----------

    @Test
    void listarSinMaterialesDevuelveVacio() throws MaterialException {
        assertTrue(sistema.listarMateriales().isEmpty());
    }

    @Test
    void listarDevuelveTodosLosMateriales() throws MaterialException {
        crearEsmalte();
        sistema.crearMaterial("Pegamento pestañas", 4, PRECIO, "Lash Pro", Categoria.LASHES, 2);

        assertEquals(2, sistema.listarMateriales().size());
    }

    // ---------- Modificar ----------

    @Test
    void modificarGuardaLosDatosNuevos() throws MaterialException {
        Material m = crearEsmalte();

        Material modificado = sistema.modificarMaterial(m.getId(), "Esmalte bordó", 20,
                new BigDecimal("400.00"), "Essie", Categoria.NAILS, 5);

        assertEquals("Esmalte bordó", modificado.getNombre());
        assertEquals(20, modificado.getStock());
        assertEquals(0, new BigDecimal("400.00").compareTo(modificado.getPrecioUnitario()));
        assertEquals("Essie", modificado.getMarca());
        assertEquals(5, modificado.getUmbralMinimo());
    }

    @Test
    void modificarFallaSiNoExiste() {
        MaterialException ex = assertThrows(MaterialException.class, () ->
            sistema.modificarMaterial(9999L, "Esmalte", 1, PRECIO, "OPI", Categoria.NAILS, 1));
        assertEquals("Material no encontrado", ex.getMessage());
    }

    // ---------- Eliminar ----------

    @Test
    void eliminarBorraElMaterial() throws MaterialException {
        Material m = crearEsmalte();

        sistema.eliminarMaterial(m.getId());

        assertFalse(repositorio.existsById(m.getId()));
        assertEquals(0, repositorio.count());
    }

    @Test
    void eliminarFallaSiNoExiste() {
        MaterialException ex = assertThrows(MaterialException.class, () ->
            sistema.eliminarMaterial(9999L));
        assertEquals("Material no encontrado", ex.getMessage());
    }
}
