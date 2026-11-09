package com.huerto.hogar;

import com.huerto.hogar.carrito.controller.CarritoController;
import com.huerto.hogar.carrito.dto.CarritoItemDto;
import com.huerto.hogar.carrito.dto.CarritoResponse;
import com.huerto.hogar.carrito.interfaces.ICarritoService;
import com.huerto.hogar.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CarritoControllerTest {
    static final String BASE = "/api/v1/entities/carrito";
    static final Principal USER = new UsernamePasswordAuthenticationToken("cli@gmail.com", null);

    @Mock ICarritoService service;
    @InjectMocks CarritoController controller;
    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private CarritoResponse carrito() {
        return CarritoResponse.builder().items(List.of()).totalItems(2).subtotal(BigDecimal.TEN)
            .descuento(BigDecimal.ONE).total(new BigDecimal("9")).build();
    }

    @Test void getCarritoSinCupon() throws Exception {
        when(service.getCarrito("cli@gmail.com", null)).thenReturn(carrito());
        mvc.perform(get(BASE).principal(USER)).andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(2));
    }

    @Test void getCarritoConCuponLoPasaAlServicio() throws Exception {
        when(service.getCarrito("cli@gmail.com", "DESC10")).thenReturn(carrito());
        mvc.perform(get(BASE).param("cupon", "DESC10").principal(USER)).andExpect(status().isOk());
        verify(service).getCarrito("cli@gmail.com", "DESC10");
    }

    @Test void getCarritoConCuponInvalidoRetorna400() throws Exception {
        when(service.getCarrito("cli@gmail.com", "MALO")).thenThrow(new RuntimeException("Cupón inválido"));
        mvc.perform(get(BASE).param("cupon", "MALO").principal(USER))
            .andExpect(status().isBadRequest()).andExpect(content().string("Cupón inválido"));
    }

    @Test void agregarOk() throws Exception {
        when(service.agregar(eq("cli@gmail.com"), any(CarritoItemDto.class))).thenReturn(carrito());
        mvc.perform(post(BASE + "/agregar").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"productoId\":1,\"cantidad\":2}"))
            .andExpect(status().isOk());
    }

    @Test void agregarConCantidadCeroRetorna400() throws Exception {
        mvc.perform(post(BASE + "/agregar").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"productoId\":1,\"cantidad\":0}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void agregarSinProductoRetorna400() throws Exception {
        mvc.perform(post(BASE + "/agregar").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"cantidad\":1}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void agregarSinStockRetorna400() throws Exception {
        when(service.agregar(eq("cli@gmail.com"), any(CarritoItemDto.class))).thenThrow(new RuntimeException("Sin stock"));
        mvc.perform(post(BASE + "/agregar").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"productoId\":1,\"cantidad\":2}"))
            .andExpect(status().isBadRequest()).andExpect(content().string("Sin stock"));
    }

    @Test void actualizarOk() throws Exception {
        when(service.actualizar("cli@gmail.com", 1L, 3)).thenReturn(carrito());
        mvc.perform(put(BASE + "/actualizar/1").param("cantidad", "3").principal(USER))
            .andExpect(status().isOk());
    }

    @Test void actualizarConErrorRetorna400() throws Exception {
        when(service.actualizar("cli@gmail.com", 1L, 99)).thenThrow(new RuntimeException("Sin stock"));
        mvc.perform(put(BASE + "/actualizar/1").param("cantidad", "99").principal(USER))
            .andExpect(status().isBadRequest());
    }

    @Test void eliminarItemOk() throws Exception {
        when(service.eliminarItem("cli@gmail.com", 1L)).thenReturn(carrito());
        mvc.perform(delete(BASE + "/eliminar/1").principal(USER)).andExpect(status().isOk());
    }

    @Test void eliminarItemConErrorRetorna400() throws Exception {
        when(service.eliminarItem("cli@gmail.com", 2L)).thenThrow(new RuntimeException("No está en el carrito"));
        mvc.perform(delete(BASE + "/eliminar/2").principal(USER)).andExpect(status().isBadRequest());
    }

    @Test void vaciarOk() throws Exception {
        mvc.perform(delete(BASE + "/vaciar").principal(USER))
            .andExpect(status().isOk()).andExpect(content().string("Carrito vaciado"));
        verify(service).vaciar("cli@gmail.com");
    }

    @Test void vaciarConErrorRetorna400() throws Exception {
        doThrow(new RuntimeException("mal")).when(service).vaciar("cli@gmail.com");
        mvc.perform(delete(BASE + "/vaciar").principal(USER)).andExpect(status().isBadRequest());
    }

}
