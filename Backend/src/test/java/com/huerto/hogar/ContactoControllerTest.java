package com.huerto.hogar;

import com.huerto.hogar.contacto.controller.ContactoController;
import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.dto.ContactoResponse;
import com.huerto.hogar.contacto.interfaces.IContactoService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ContactoControllerTest {
    static final String BASE = "/api/v1/entities/contacto";
    static final String JSON_OK = "{\"nombre\":\"Ana\",\"correo\":\"ana@gmail.com\",\"comentario\":\"Hola\"}";

    @Mock IContactoService service;
    @InjectMocks ContactoController controller;
    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private ContactoResponse contacto() {
        ContactoResponse r = new ContactoResponse();
        r.setId(1L); r.setNombre("Ana"); r.setCorreo("ana@gmail.com"); r.setComentario("Hola");
        return r;
    }

    @Test void saveRegistraMensajeConStatus201() throws Exception {
        when(service.save(any(ContactoDto.class))).thenReturn(contacto());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.nombre").value("Ana"));
        ArgumentCaptor<ContactoDto> cap = ArgumentCaptor.forClass(ContactoDto.class);
        verify(service).save(cap.capture());
        assertEquals("Hola", cap.getValue().getComentario());
    }

    @Test void saveConCorreoInvalidoRetorna400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Ana\",\"correo\":\"x\",\"comentario\":\"Hola\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void saveConDominioNoPermitidoRetorna400() throws Exception {
        when(service.save(any(ContactoDto.class))).thenThrow(new RuntimeException("Dominio no permitido"));
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(JSON_OK))
            .andExpect(status().isBadRequest()).andExpect(content().string("Dominio no permitido"));
    }

    @Test void findAllOk() throws Exception {
        when(service.findAll()).thenReturn(List.of(contacto()));
        mvc.perform(get(BASE)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void findAllConErrorRetorna400() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("mal"));
        mvc.perform(get(BASE)).andExpect(status().isBadRequest());
    }

    @Test void marcarLeidoOk() throws Exception {
        ContactoResponse c = contacto();
        c.setLeido(true);
        when(service.marcarLeido(1L)).thenReturn(c);
        mvc.perform(patch(BASE + "/1/leido")).andExpect(status().isOk()).andExpect(jsonPath("$.leido").value(true));
    }

    @Test void marcarLeidoInexistenteRetorna404() throws Exception {
        when(service.marcarLeido(5L)).thenThrow(new RecursoNoEncontradoException("Contacto no encontrado"));
        mvc.perform(patch(BASE + "/5/leido")).andExpect(status().isNotFound());
    }

    @Test void marcarLeidoConErrorGenericoRetorna400() throws Exception {
        when(service.marcarLeido(6L)).thenThrow(new RuntimeException("mal"));
        mvc.perform(patch(BASE + "/6/leido")).andExpect(status().isBadRequest());
    }
}
