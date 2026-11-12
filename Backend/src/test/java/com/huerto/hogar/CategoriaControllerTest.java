package com.huerto.hogar;

import com.huerto.hogar.categoria.controller.CategoriaController;
import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.dto.CategoriaResponse;
import com.huerto.hogar.categoria.interfaces.ICategoriaService;
import com.huerto.hogar.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {
    static final String BASE = "/api/v1/entities/categorias";
    static final String JSON_OK = "{\"nombre\":\"Frutas\",\"descripcion\":\"Frescas\"}";

    @Mock ICategoriaService service;
    @InjectMocks CategoriaController controller;
    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private CategoriaResponse cat() {
        CategoriaResponse r = new CategoriaResponse();
        r.setId(1L); r.setNombre("Frutas"); r.setDescripcion("Frescas");
        return r;
    }

    @Test void findAllOk() throws Exception {
        when(service.findAll()).thenReturn(List.of(cat()));
        mvc.perform(get(BASE)).andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Frutas"));
    }

    @Test void findAllConErrorRetorna404() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("falla"));
        mvc.perform(get(BASE)).andExpect(status().isNotFound());
    }

    @Test void findByIdOk() throws Exception {
        when(service.findById(1L)).thenReturn(cat());
        mvc.perform(get(BASE + "/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void findByIdInexistenteRetorna404() throws Exception {
        when(service.findById(8L)).thenThrow(new RuntimeException("Categoría no encontrada"));
        mvc.perform(get(BASE + "/8")).andExpect(status().isNotFound()).andExpect(content().string("Categoría no encontrada"));
    }

    @Test void saveRetorna201() throws Exception {
        when(service.save(org.mockito.ArgumentMatchers.any(CategoriaDto.class))).thenReturn(cat());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isCreated());
    }

    @Test void saveSinNombreRetorna400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{\"descripcion\":\"x\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void saveDuplicadaRetorna400() throws Exception {
        when(service.save(org.mockito.ArgumentMatchers.any(CategoriaDto.class))).thenThrow(new RuntimeException("Ya existe"));
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isBadRequest()).andExpect(content().string("Ya existe"));
    }

    @Test void updateOk() throws Exception {
        when(service.update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(CategoriaDto.class))).thenReturn(cat());
        mvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isOk());
    }

    @Test void updateConErrorRetorna400() throws Exception {
        when(service.update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(CategoriaDto.class))).thenThrow(new RuntimeException("mal"));
        mvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isBadRequest());
    }

    @Test void deleteOk() throws Exception {
        mvc.perform(delete(BASE + "/1")).andExpect(status().isOk()).andExpect(content().string("Categoría eliminada"));
        verify(service).deleteById(1L);
    }

    @Test void deleteInexistenteRetorna404() throws Exception {
        doThrow(new RuntimeException("no existe")).when(service).deleteById(3L);
        mvc.perform(delete(BASE + "/3")).andExpect(status().isNotFound());
    }
}
