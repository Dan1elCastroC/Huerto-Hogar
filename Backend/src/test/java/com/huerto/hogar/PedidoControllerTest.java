package com.huerto.hogar;

import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.pedido.controller.PedidoController;
import com.huerto.hogar.pedido.dto.CheckoutRequest;
import com.huerto.hogar.pedido.dto.PedidoResponse;
import com.huerto.hogar.pedido.interfaces.IPedidoService;
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

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {
    static final String BASE = "/api/v1/entities/pedidos";
    static final Principal USER = new UsernamePasswordAuthenticationToken("cli@gmail.com", null);

    @Mock IPedidoService service;
    @InjectMocks PedidoController controller;
    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private PedidoResponse pedido() { return PedidoResponse.builder().id(1L).estado("PENDIENTE").build(); }

    @Test void checkoutCreaPedidoConStatus201() throws Exception {
        when(service.checkout(eq("cli@gmail.com"), any(CheckoutRequest.class))).thenReturn(pedido());
        mvc.perform(post(BASE + "/checkout").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"direccionEntrega\":\"Calle 1\",\"codigoCupon\":\"DESC10\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test void checkoutSinDireccionRetorna400() throws Exception {
        mvc.perform(post(BASE + "/checkout").principal(USER).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void checkoutConCarritoVacioRetorna400() throws Exception {
        when(service.checkout(eq("cli@gmail.com"), any(CheckoutRequest.class)))
            .thenThrow(new RuntimeException("Carrito vacío"));
        mvc.perform(post(BASE + "/checkout").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"direccionEntrega\":\"Calle 1\"}"))
            .andExpect(status().isBadRequest()).andExpect(content().string("Carrito vacío"));
    }

    @Test void historialOk() throws Exception {
        when(service.historial("cli@gmail.com")).thenReturn(List.of(pedido()));
        mvc.perform(get(BASE + "/mis-pedidos").principal(USER)).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test void historialConErrorRetorna400() throws Exception {
        when(service.historial("cli@gmail.com")).thenThrow(new RuntimeException("mal"));
        mvc.perform(get(BASE + "/mis-pedidos").principal(USER)).andExpect(status().isBadRequest());
    }

    @Test void findByIdPropioOk() throws Exception {
        when(service.findById(1L, "cli@gmail.com")).thenReturn(pedido());
        mvc.perform(get(BASE + "/mis-pedidos/1").principal(USER)).andExpect(status().isOk());
    }

    @Test void findByIdAjenoRetorna404() throws Exception {
        when(service.findById(2L, "cli@gmail.com")).thenThrow(new RuntimeException("Pedido no encontrado"));
        mvc.perform(get(BASE + "/mis-pedidos/2").principal(USER)).andExpect(status().isNotFound());
    }

    @Test void findAllOk() throws Exception {
        when(service.findAll()).thenReturn(List.of(pedido()));
        mvc.perform(get(BASE)).andExpect(status().isOk());
    }

    @Test void findAllConErrorRetorna400() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("mal"));
        mvc.perform(get(BASE)).andExpect(status().isBadRequest());
    }

    @Test void findByEstadoOk() throws Exception {
        when(service.findByEstado("PENDIENTE")).thenReturn(List.of(pedido()));
        mvc.perform(get(BASE + "/estado/PENDIENTE")).andExpect(status().isOk());
    }

    @Test void findByEstadoInvalidoRetorna400() throws Exception {
        when(service.findByEstado("XYZ")).thenThrow(new RuntimeException("Estado inválido"));
        mvc.perform(get(BASE + "/estado/XYZ")).andExpect(status().isBadRequest())
            .andExpect(content().string("Estado inválido"));
    }

    @Test void cambiarEstadoOk() throws Exception {
        when(service.cambiarEstado(1L, "ENVIADO")).thenReturn(pedido());
        mvc.perform(patch(BASE + "/1/estado").param("estado", "ENVIADO")).andExpect(status().isOk());
    }

    @Test void cambiarEstadoInvalidoRetorna400() throws Exception {
        when(service.cambiarEstado(1L, "XYZ")).thenThrow(new RuntimeException("Estado inválido"));
        mvc.perform(patch(BASE + "/1/estado").param("estado", "XYZ")).andExpect(status().isBadRequest());
    }
}
