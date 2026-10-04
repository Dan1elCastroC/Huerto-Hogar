package com.huerto.hogar;

import com.huerto.hogar.contacto.entity.ContactoEntity;
import com.huerto.hogar.contacto.repository.ContactoRepository;
import com.huerto.hogar.contacto.service.ContactoService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {
    @Mock ContactoRepository repo;
    @InjectMocks ContactoService service;

    @Test void saveNormalizaCorreoYNombra() {
        ContactoEntity c = ContactoEntity.builder().nombre("  Juan  ").correo(" JUAN@GMAIL.COM ").comentario("  Hola  ").build();
        when(repo.save(c)).thenReturn(c);
        service.save(c);
        assertEquals("juan@gmail.com", c.getCorreo());
        assertEquals("Juan", c.getNombre());
        assertEquals("Hola", c.getComentario());
        assertFalse(c.isLeido());
        verify(repo).save(c);
    }

    @Test void saveRechazaCorreoNoPermitido() {
        ContactoEntity c = ContactoEntity.builder().nombre("Juan").correo("juan@hotmail.com").comentario("Hola").build();
        assertThrows(ReglaDeNegocioException.class, () -> service.save(c));
        verify(repo, never()).save(any());
    }

    @Test void marcarLeidoCambiaEstado() {
        ContactoEntity c = ContactoEntity.builder().id(1L).nombre("Juan").correo("juan@gmail.com").comentario("Hola").build();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(repo.save(c)).thenReturn(c);
        service.marcarLeido(1L);
        assertTrue(c.isLeido());
        verify(repo).save(c);
    }

    @Test void marcarLeidoInexistenteLanzaExcepcion() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.marcarLeido(1L));
    }
}
