package com.huerto.hogar;

import com.huerto.hogar.blog.controller.BlogController;
import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.dto.BlogResponse;
import com.huerto.hogar.blog.interfaces.IBlogService;
import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BlogControllerTest {
    static final String BASE = "/api/v1/entities/blogs";
    static final String JSON_OK = "{\"titulo\":\"Huerta\",\"descripcion\":\"Texto\",\"imagenUrl\":\"http://i/x.jpg\"}";

    @Mock IBlogService service;
    @InjectMocks BlogController controller;
    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private BlogResponse blog() {
        BlogResponse r = new BlogResponse();
        r.setId(1L); r.setTitulo("Huerta"); r.setDescripcion("Texto");
        return r;
    }

    @Test void findAllOk() throws Exception {
        when(service.findAll()).thenReturn(List.of(blog()));
        mvc.perform(get(BASE)).andExpect(status().isOk()).andExpect(jsonPath("$[0].titulo").value("Huerta"));
    }

    @Test void findAllConErrorRetorna400() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("falla"));
        mvc.perform(get(BASE)).andExpect(status().isBadRequest());
    }

    @Test void findByIdOk() throws Exception {
        when(service.findById(1L)).thenReturn(blog());
        mvc.perform(get(BASE + "/1")).andExpect(status().isOk());
    }

    @Test void findByIdInexistenteRetorna404() throws Exception {
        when(service.findById(2L)).thenThrow(new RecursoNoEncontradoException("Blog no encontrado"));
        mvc.perform(get(BASE + "/2")).andExpect(status().isNotFound()).andExpect(content().string("Blog no encontrado"));
    }

    @Test void findByIdConErrorGenericoRetorna400() throws Exception {
        when(service.findById(3L)).thenThrow(new RuntimeException("mal"));
        mvc.perform(get(BASE + "/3")).andExpect(status().isBadRequest());
    }

    @Test void saveEntregaElDtoAlServicioYRetorna201() throws Exception {
        when(service.save(any(BlogDto.class))).thenReturn(blog());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isCreated());
        ArgumentCaptor<BlogDto> cap = ArgumentCaptor.forClass(BlogDto.class);
        verify(service).save(cap.capture());
        assertEquals("Huerta", cap.getValue().getTitulo());
        assertEquals("http://i/x.jpg", cap.getValue().getImagenUrl());
    }

    @Test void saveSinTituloRetorna400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{\"descripcion\":\"x\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void saveConErrorDelServicioRetorna400() throws Exception {
        when(service.save(any(BlogDto.class))).thenThrow(new RuntimeException("mal"));
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isBadRequest());
    }

    @Test void updateSinCampoActivoLoDejaActivo() throws Exception {
        when(service.update(eq(1L), any(BlogDto.class))).thenReturn(blog());
        mvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isOk());
        ArgumentCaptor<BlogDto> cap = ArgumentCaptor.forClass(BlogDto.class);
        verify(service).update(eq(1L), cap.capture());
        assertNull(cap.getValue().getActivo()); // el servicio lo interpreta como activo
    }

    @Test void updateConActivoFalsoLoDesactiva() throws Exception {
        when(service.update(eq(1L), any(BlogDto.class))).thenReturn(blog());
        mvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"H\",\"descripcion\":\"T\",\"activo\":false}"))
            .andExpect(status().isOk());
        ArgumentCaptor<BlogDto> cap = ArgumentCaptor.forClass(BlogDto.class);
        verify(service).update(eq(1L), cap.capture());
        assertFalse(cap.getValue().getActivo());
    }

    @Test void updateInexistenteRetorna404() throws Exception {
        when(service.update(eq(9L), any(BlogDto.class))).thenThrow(new RecursoNoEncontradoException("Blog no encontrado"));
        mvc.perform(put(BASE + "/9").contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isNotFound());
    }

    @Test void updateConErrorGenericoRetorna400() throws Exception {
        when(service.update(eq(9L), any(BlogDto.class))).thenThrow(new RuntimeException("mal"));
        mvc.perform(put(BASE + "/9").contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isBadRequest());
    }

    @Test void deleteOk() throws Exception {
        mvc.perform(delete(BASE + "/1")).andExpect(status().isOk()).andExpect(content().string("Blog desactivado"));
        verify(service).deleteById(1L);
    }

    @Test void deleteInexistenteRetorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("no")).when(service).deleteById(4L);
        mvc.perform(delete(BASE + "/4")).andExpect(status().isNotFound());
    }
}
