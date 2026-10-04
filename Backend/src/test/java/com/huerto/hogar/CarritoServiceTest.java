package com.huerto.hogar;

import com.huerto.hogar.carrito.dto.CarritoItemDto;
import com.huerto.hogar.carrito.entity.CarritoItemEntity;
import com.huerto.hogar.carrito.repository.CarritoRepository;
import com.huerto.hogar.carrito.service.CarritoService;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
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
class CarritoServiceTest {
    @Mock CarritoRepository carritoRepo;
    @Mock ProductoRepository productoRepo;
    @Mock UsuarioRepository usuarioRepo;
    @Mock ICuponService cuponService;
    @InjectMocks CarritoService service;

    private UsuarioEntity usuario() { return UsuarioEntity.builder().id(1L).correo("juan@gmail.com").build(); }
    private ProductoEntity producto() { return ProductoEntity.builder().id(2L).nombre("Manzana").precio(new BigDecimal("1000")).stock(10).activo(true).build(); }
    private CarritoItemDto dto(int cantidad) { CarritoItemDto d = new CarritoItemDto(); d.setProductoId(2L); d.setCantidad(cantidad); return d; }

    @Test void agregarRechazaUsuarioInexistente() {
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.agregar("juan@gmail.com", dto(2)));
    }

    @Test void agregarRechazaProductoInactivo() {
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(usuario()));
        ProductoEntity p = producto(); p.setActivo(false);
        when(productoRepo.findById(2L)).thenReturn(Optional.of(p));
        assertThrows(ReglaDeNegocioException.class, () -> service.agregar("juan@gmail.com", dto(2)));
    }

    @Test void agregarRechazaStockInsuficiente() {
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(usuario()));
        ProductoEntity p = producto(); p.setStock(1);
        when(productoRepo.findById(2L)).thenReturn(Optional.of(p));
        assertThrows(ReglaDeNegocioException.class, () -> service.agregar("juan@gmail.com", dto(2)));
    }

    @Test void agregarCreaItemNuevo() {
        UsuarioEntity u = usuario(); ProductoEntity p = producto();
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(u));
        when(productoRepo.findById(2L)).thenReturn(Optional.of(p));
        when(carritoRepo.findByUsuarioIdAndProductoId(1L, 2L)).thenReturn(Optional.empty());
        CarritoItemEntity item = CarritoItemEntity.builder().id(1L).usuario(u).producto(p).cantidad(2).precioUnitario(p.getPrecio()).build();
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(List.of(item));
        assertEquals(2, service.agregar("juan@gmail.com", dto(2)).getTotalItems());
        verify(carritoRepo).save(any(CarritoItemEntity.class));
    }

    @Test void actualizarConCantidadCeroEliminaItem() {
        UsuarioEntity u = usuario(); ProductoEntity p = producto();
        CarritoItemEntity item = CarritoItemEntity.builder().id(1L).usuario(u).producto(p).cantidad(2).precioUnitario(p.getPrecio()).build();
        when(usuarioRepo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(u));
        when(carritoRepo.findByUsuarioIdAndProductoId(1L, 2L)).thenReturn(Optional.of(item));
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(List.of());
        service.actualizar("juan@gmail.com", 2L, 0);
        verify(carritoRepo).delete(item);
    }
}
