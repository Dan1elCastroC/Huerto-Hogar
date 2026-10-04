package com.huerto.hogar;

import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import com.huerto.hogar.producto.dto.ProductoDto;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
import com.huerto.hogar.producto.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock ProductoRepository productoRepo;
    @Mock CategoriaRepository categoriaRepo;
    @InjectMocks ProductoService service;

    private CategoriaEntity categoria() {
        return CategoriaEntity.builder().id(1L).nombre("Frutas").build();
    }

    private ProductoDto dto() {
        ProductoDto d = new ProductoDto();
        d.setCodigo("P001"); d.setNombre("Manzana"); d.setDescripcion("Roja");
        d.setPrecio(new BigDecimal("1000")); d.setStock(10); d.setStockCritico(2);
        d.setImagenUrl("img.jpg"); d.setCategoriaId(1L);
        return d;
    }

    private ProductoEntity producto() {
        return ProductoEntity.builder().id(1L).codigo("P001").nombre("Manzana")
            .descripcion("Roja").precio(new BigDecimal("1000")).stock(10).stockCritico(2)
            .imagenUrl("img.jpg").categoria(categoria()).activo(true).build();
    }

    @Test void findByIdDevuelveProducto() {
        ProductoEntity p = producto();
        when(productoRepo.findById(1L)).thenReturn(Optional.of(p));
        assertEquals("P001", service.findById(1L).getCodigo());
        assertEquals("Frutas", service.findById(1L).getCategoriaNombre());
    }

    @Test void findByIdInexistenteLanzaExcepcion() {
        when(productoRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(99L));
    }

    @Test void saveNoPermiteCodigoRepetido() {
        when(productoRepo.existsByCodigo("P001")).thenReturn(true);
        assertThrows(ReglaDeNegocioException.class, () -> service.save(dto()));
        verify(productoRepo, never()).save(any());
    }

    @Test void saveRechazaCategoriaInexistente() {
        when(productoRepo.existsByCodigo("P001")).thenReturn(false);
        when(categoriaRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.save(dto()));
    }

    @Test void saveCreaProductoCorrectamente() {
        ProductoDto d = dto();
        ProductoEntity saved = producto();
        when(productoRepo.existsByCodigo("P001")).thenReturn(false);
        when(categoriaRepo.findById(1L)).thenReturn(Optional.of(categoria()));
        when(productoRepo.save(any(ProductoEntity.class))).thenReturn(saved);
        assertEquals("Manzana", service.save(d).getNombre());
        verify(productoRepo).save(any(ProductoEntity.class));
    }

    @Test void deleteDesactivaProducto() {
        ProductoEntity p = producto();
        when(productoRepo.findById(1L)).thenReturn(Optional.of(p));
        service.deleteById(1L);
        assertFalse(p.isActivo());
        verify(productoRepo).save(p);
    }
}
