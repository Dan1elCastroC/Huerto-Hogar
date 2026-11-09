package com.huerto.hogar;

import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.producto.controller.ProductoController;
import com.huerto.hogar.producto.dto.ProductoDto;
import com.huerto.hogar.producto.dto.ProductoResponse;
import com.huerto.hogar.producto.interfaces.IProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {
    static final String BASE = "/api/v1/entities/productos";
    static final String JSON_OK = "{\"codigo\":\"FR001\",\"nombre\":\"Manzana\",\"precio\":1200,\"stock\":5,\"categoriaId\":1}";

    @Mock IProductoService service;
    @InjectMocks ProductoController controller;
    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private ProductoResponse producto() {
        return ProductoResponse.builder().id(1L).codigo("FR001").nombre("Manzana")
            .precio(new BigDecimal("1200")).stock(5).categoriaId(1L).activo(true).build();
    }

    @Test void findAllDevuelveListado() throws Exception {
        when(service.findAll()).thenReturn(List.of(producto()));
        mvc.perform(get(BASE)).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codigo").value("FR001"));
    }

    @Test void findAllConErrorRetorna404() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("falla"));
        mvc.perform(get(BASE)).andExpect(status().isNotFound()).andExpect(content().string("falla"));
    }

    @Test void findByIdDevuelveProducto() throws Exception {
        when(service.findById(1L)).thenReturn(producto());
        mvc.perform(get(BASE + "/1")).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Manzana"));
    }

    @Test void findByIdInexistenteRetorna404() throws Exception {
        when(service.findById(99L)).thenThrow(new RecursoNoEncontradoException("Producto no encontrado"));
        mvc.perform(get(BASE + "/99")).andExpect(status().isNotFound())
            .andExpect(content().string("Producto no encontrado"));
    }

    @Test void buscarDevuelveCoincidencias() throws Exception {
        when(service.buscar("man")).thenReturn(List.of(producto()));
        mvc.perform(get(BASE + "/buscar").param("q", "man")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test void buscarConErrorRetorna400() throws Exception {
        when(service.buscar("x")).thenThrow(new RuntimeException("mal"));
        mvc.perform(get(BASE + "/buscar").param("q", "x")).andExpect(status().isBadRequest());
    }

    @Test void findByCategoriaOk() throws Exception {
        when(service.findByCategoria(2L)).thenReturn(List.of(producto()));
        mvc.perform(get(BASE + "/categoria/2")).andExpect(status().isOk());
    }

    @Test void findByCategoriaInexistenteRetorna404() throws Exception {
        when(service.findByCategoria(9L)).thenThrow(new RecursoNoEncontradoException("Categoría no encontrada"));
        mvc.perform(get(BASE + "/categoria/9")).andExpect(status().isNotFound());
    }

    @Test void stockCriticoOk() throws Exception {
        when(service.findStockCritico()).thenReturn(List.of(producto()));
        mvc.perform(get(BASE + "/stock-critico")).andExpect(status().isOk());
    }

    @Test void stockCriticoConErrorRetorna400() throws Exception {
        when(service.findStockCritico()).thenThrow(new RuntimeException("mal"));
        mvc.perform(get(BASE + "/stock-critico")).andExpect(status().isBadRequest());
    }

    @Test void saveCreaProductoConStatus201() throws Exception {
        when(service.save(any(ProductoDto.class))).thenReturn(producto());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void saveConDatosInvalidosRetorna400SinLlamarAlServicio() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void saveConReglaDeNegocioRetorna400() throws Exception {
        when(service.save(any(ProductoDto.class))).thenThrow(new RuntimeException("Código duplicado"));
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isBadRequest()).andExpect(content().string("Código duplicado"));
    }

    @Test void updateOk() throws Exception {
        when(service.update(eq(1L), any(ProductoDto.class))).thenReturn(producto());
        mvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isOk());
    }

    @Test void updateConErrorRetorna400() throws Exception {
        when(service.update(eq(1L), any(ProductoDto.class))).thenThrow(new RuntimeException("mal"));
        mvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isBadRequest());
    }

    @Test void deleteOk() throws Exception {
        mvc.perform(delete(BASE + "/1")).andExpect(status().isOk()).andExpect(content().string("Producto eliminado"));
        verify(service).deleteById(1L);
    }

    @Test void deleteInexistenteRetorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("Producto no encontrado")).when(service).deleteById(5L);
        mvc.perform(delete(BASE + "/5")).andExpect(status().isNotFound());
    }
}
