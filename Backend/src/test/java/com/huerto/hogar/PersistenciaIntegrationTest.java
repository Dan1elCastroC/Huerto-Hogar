package com.huerto.hogar;

import com.huerto.hogar.carrito.dto.CarritoItemDto;
import com.huerto.hogar.carrito.service.CarritoService;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.config.DataInitializer;
import com.huerto.hogar.pedido.dto.CheckoutRequest;
import com.huerto.hogar.pedido.dto.PedidoResponse;
import com.huerto.hogar.pedido.service.PedidoService;
import com.huerto.hogar.producto.dto.ProductoDto;
import com.huerto.hogar.producto.dto.ProductoResponse;
import com.huerto.hogar.producto.repository.ProductoRepository;
import com.huerto.hogar.producto.service.ProductoService;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PersistenciaIntegrationTest {
    @Autowired ProductoService productos;
    @Autowired ProductoRepository productoRepo;
    @Autowired CategoriaRepository categoriaRepo;
    @Autowired UsuarioRepository usuarioRepo;
    @Autowired CarritoService carrito;
    @Autowired PedidoService pedidos;
    @Autowired DataInitializer initializer;

    @Test
    void catalogoInicialSePueblaSinDuplicarseAlReiniciar() {
        long productosAntes = productoRepo.count();
        long categoriasAntes = categoriaRepo.count();
        long usuariosAntes = usuarioRepo.count();

        initializer.run();

        assertAll(
            () -> assertTrue(productoRepo.findByCodigo("KIT-001").isPresent()),
            () -> assertTrue(productoRepo.findByCodigo("COM-001").isPresent()),
            () -> assertTrue(productoRepo.findByCodigo("HER-001").isPresent()),
            () -> assertTrue(productoRepo.findByCodigo("SUS-001").isPresent()),
            () -> assertTrue(productoRepo.findByCodigo("FRU-001").isPresent()),
            () -> assertTrue(productoRepo.findByCodigo("VER-001").isPresent()),
            () -> assertTrue(productoRepo.findByCodigo("HIE-001").isPresent()),
            () -> assertTrue(productoRepo.findByCodigo("SEM-001").isPresent()),
            () -> assertEquals(productosAntes, productoRepo.count()),
            () -> assertEquals(categoriasAntes, categoriaRepo.count()),
            () -> assertEquals(usuariosAntes, usuarioRepo.count())
        );
    }

    // Sin transacción de test: cada llamada debe funcionar con open-in-view=false.
    @Test
    void productoGuardadoSePuedeConsultarConSuCategoria() {
        ProductoResponse creado = crearProducto();

        assertAll(
            () -> comprobarProducto(productos.findById(creado.getId()), creado),
            () -> comprobarProducto(encontrar(productos.findAll(), creado.getId()), creado),
            () -> comprobarProducto(encontrar(productos.buscar(creado.getNombre()), creado.getId()), creado),
            () -> comprobarProducto(encontrar(productos.findByCategoria(creado.getCategoriaId()), creado.getId()), creado),
            () -> comprobarProducto(encontrar(productos.findStockCritico(), creado.getId()), creado)
        );
    }

    @Test
    void compraGuardadaSePuedeConsultarConSusDetalles() {
        ProductoResponse producto = crearProducto();
        String identificador = UUID.randomUUID().toString().substring(0, 8);
        UsuarioEntity usuario = usuarioRepo.save(UsuarioEntity.builder()
            .run(identificador.toUpperCase() + "K").nombre("Prueba").apellidos("Persistencia")
            .correo(identificador + "@gmail.com").contrasena("solo-prueba")
            .direccion("Calle Prueba 123").region("Biobío").comuna("Concepción").build());
        CarritoItemDto item = new CarritoItemDto();
        item.setProductoId(producto.getId());
        item.setCantidad(2);
        carrito.agregar(usuario.getCorreo(), item);
        CheckoutRequest checkout = new CheckoutRequest();
        checkout.setDireccionEntrega(usuario.getDireccion());

        PedidoResponse creado = pedidos.checkout(usuario.getCorreo(), checkout);

        assertAll(
            () -> comprobarPedido(pedidos.findById(creado.getId(), usuario.getCorreo()), producto.getId()),
            () -> comprobarPedido(pedidos.historial(usuario.getCorreo()).stream()
                .filter(p -> p.getId().equals(creado.getId())).findFirst().orElseThrow(), producto.getId()),
            () -> comprobarPedido(pedidos.findAll().stream()
                .filter(p -> p.getId().equals(creado.getId())).findFirst().orElseThrow(), producto.getId()),
            () -> comprobarPedido(pedidos.findByEstado("PENDIENTE").stream()
                .filter(p -> p.getId().equals(creado.getId())).findFirst().orElseThrow(), producto.getId()),
            () -> assertEquals(2, productoRepo.findById(producto.getId()).orElseThrow().getStock()),
            () -> assertTrue(carrito.getCarrito(usuario.getCorreo(), null).getItems().isEmpty())
        );
    }

    private ProductoResponse crearProducto() {
        String identificador = UUID.randomUUID().toString();
        CategoriaEntity categoria = categoriaRepo.save(CategoriaEntity.builder()
            .nombre("Categoría " + identificador).build());
        ProductoDto dto = new ProductoDto();
        dto.setCodigo("INT-" + identificador);
        dto.setNombre("Producto " + identificador);
        dto.setDescripcion("Prueba de persistencia real");
        dto.setPrecio(new BigDecimal("1250.00"));
        dto.setStock(4);
        dto.setStockCritico(5);
        dto.setCategoriaId(categoria.getId());
        return productos.save(dto);
    }

    private ProductoResponse encontrar(List<ProductoResponse> lista, Long id) {
        return lista.stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow();
    }

    private void comprobarProducto(ProductoResponse actual, ProductoResponse esperado) {
        assertEquals(esperado.getId(), actual.getId());
        assertEquals(esperado.getCategoriaNombre(), actual.getCategoriaNombre());
        assertEquals(0, new BigDecimal("1250.00").compareTo(actual.getPrecio()));
    }

    private void comprobarPedido(PedidoResponse pedido, Long productoId) {
        assertEquals("PENDIENTE", pedido.getEstado());
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(productoId, pedido.getDetalles().get(0).getProductoId());
        assertEquals(2, pedido.getDetalles().get(0).getCantidad());
        assertEquals(0, new BigDecimal("2500.00").compareTo(pedido.getTotal()));
    }
}
