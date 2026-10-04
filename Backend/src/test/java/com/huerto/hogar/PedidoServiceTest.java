package com.huerto.hogar;

import com.huerto.hogar.carrito.entity.CarritoItemEntity;
import com.huerto.hogar.carrito.interfaces.ICarritoService;
import com.huerto.hogar.carrito.repository.CarritoRepository;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import com.huerto.hogar.pedido.dto.CheckoutRequest;
import com.huerto.hogar.pedido.entity.PedidoEntity;
import com.huerto.hogar.pedido.entity.EstadoPedido;
import com.huerto.hogar.pedido.repository.PedidoRepository;
import com.huerto.hogar.pedido.service.PedidoService;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    @Mock PedidoRepository pedidoRepo;
    @Mock CarritoRepository carritoRepo;
    @Mock UsuarioRepository usuarioRepo;
    @Mock ICuponService cuponService;
    @Mock ICarritoService carritoService;
    @InjectMocks PedidoService service;

    private UsuarioEntity usuario() { return UsuarioEntity.builder().id(1L).correo("juan@gmail.com").nombre("Juan").apellidos("Perez").rol(com.huerto.hogar.usuario.entity.Rol.CLIENTE).build(); }
    private ProductoEntity producto() { return ProductoEntity.builder().id(2L).nombre("Manzana").precio(new BigDecimal("1000")).stock(10).activo(true).build(); }
    private CheckoutRequest checkout() { CheckoutRequest r = new CheckoutRequest(); r.setDireccionEntrega("Calle 1"); r.setFechaEntregaDeseada("2030-01-01"); return r; }

    @Test void checkoutRechazaCarritoVacio() {
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(usuario()));
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(List.of());
        assertThrows(ReglaDeNegocioException.class, () -> service.checkout("juan@gmail.com", checkout()));
    }

    @Test void checkoutRechazaStockInsuficiente() {
        UsuarioEntity u = usuario(); ProductoEntity p = producto(); p.setStock(1);
        CarritoItemEntity item = CarritoItemEntity.builder().id(1L).usuario(u).producto(p).cantidad(2).precioUnitario(new BigDecimal("1000")).build();
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(u));
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(List.of(item));
        assertThrows(ReglaDeNegocioException.class, () -> service.checkout("juan@gmail.com", checkout()));
    }

    @Test void checkoutCreaPedidoYVacíaCarrito() {
        UsuarioEntity u = usuario(); ProductoEntity p = producto();
        CarritoItemEntity item = CarritoItemEntity.builder().id(1L).usuario(u).producto(p).cantidad(2).precioUnitario(new BigDecimal("1000")).build();
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(u));
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(List.of(item));
        when(pedidoRepo.save(any(PedidoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("PENDIENTE", service.checkout("juan@gmail.com", checkout()).getEstado());
        assertEquals(8, p.getStock());
        verify(pedidoRepo).save(any(PedidoEntity.class));
        verify(carritoService).vaciar("juan@gmail.com");
    }

    @Test void findByEstadoRechazaEstadoInvalido() {
        assertThrows(ReglaDeNegocioException.class, () -> service.findByEstado("NO_EXISTE"));
    }

    @Test void cambiarEstadoActualizaPedido() {
        UsuarioEntity u = usuario();
        PedidoEntity p = PedidoEntity.builder().id(1L).usuario(u).subtotal(new BigDecimal("1000"))
            .descuento(BigDecimal.ZERO).total(new BigDecimal("1000")).direccionEntrega("Calle 1")
            .estado(EstadoPedido.PENDIENTE).build();
        when(pedidoRepo.findById(1L)).thenReturn(Optional.of(p));
        when(pedidoRepo.save(p)).thenReturn(p);
        assertEquals("ENVIADO", service.cambiarEstado(1L, "ENVIADO").getEstado());
        verify(pedidoRepo).save(p);
    }

    @Test void cambiarEstadoRechazaEstadoInvalido() {
        UsuarioEntity u = usuario();
        PedidoEntity p = PedidoEntity.builder().id(1L).usuario(u).subtotal(new BigDecimal("1000"))
            .descuento(BigDecimal.ZERO).total(new BigDecimal("1000")).direccionEntrega("Calle 1")
            .estado(EstadoPedido.PENDIENTE).build();
        when(pedidoRepo.findById(1L)).thenReturn(Optional.of(p));
        assertThrows(ReglaDeNegocioException.class, () -> service.cambiarEstado(1L, "NO_EXISTE"));
    }

    @Test void findByIdInexistenteLanzaExcepcion() {
        when(pedidoRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(99L, "juan@gmail.com"));
    }
}
