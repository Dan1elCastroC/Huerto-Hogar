package com.huerto.hogar;

import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.dto.ContactoResponse;
import com.huerto.hogar.contacto.entity.ContactoEntity;
import com.huerto.hogar.contacto.mapper.ContactoMapper;
import com.huerto.hogar.contacto.repository.ContactoRepository;
import com.huerto.hogar.contacto.service.ContactoService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {
    @Mock ContactoRepository repo;
    // Mapper real (no un spy): así el código generado por MapStruct también cuenta en JaCoCo.
    final ContactoMapper mapper = Mappers.getMapper(ContactoMapper.class);
    ContactoService service;

    @BeforeEach void preparar() { service = new ContactoService(repo, mapper); }

    private ContactoDto dto(String nombre, String correo, String comentario) {
        ContactoDto d = new ContactoDto();
        d.setNombre(nombre); d.setCorreo(correo); d.setComentario(comentario);
        return d;
    }

    @Test void saveNormalizaCorreoYNombreYDevuelveDto() {
        when(repo.save(any(ContactoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        ContactoResponse r = service.save(dto("  Juan  ", " JUAN@GMAIL.COM ", "  Hola  "));
        ArgumentCaptor<ContactoEntity> cap = ArgumentCaptor.forClass(ContactoEntity.class);
        verify(repo).save(cap.capture());
        ContactoEntity guardada = cap.getValue();
        assertEquals("juan@gmail.com", guardada.getCorreo());
        assertEquals("Juan", guardada.getNombre());
        assertEquals("Hola", guardada.getComentario());
        assertFalse(guardada.isLeido());
        assertEquals("juan@gmail.com", r.getCorreo());
        assertEquals("Juan", r.getNombre());
        assertFalse(r.isLeido());
    }

    @Test void saveRechazaCorreoNoPermitido() {
        assertThrows(ReglaDeNegocioException.class, () -> service.save(dto("Juan", "juan@hotmail.com", "Hola")));
        verify(repo, never()).save(any());
    }

    @Test void findAllConvierteLasEntidadesEnDtos() {
        when(repo.findAllByOrderByCreadoEnDesc()).thenReturn(List.of(
            ContactoEntity.builder().id(1L).nombre("Ana").correo("ana@gmail.com").comentario("Hola").build()));
        List<ContactoResponse> lista = service.findAll();
        assertEquals(1, lista.size());
        assertEquals("Ana", lista.get(0).getNombre());
        assertEquals(1L, lista.get(0).getId());
    }

    @Test void marcarLeidoCambiaEstado() {
        ContactoEntity c = ContactoEntity.builder().id(1L).nombre("Juan").correo("juan@gmail.com").comentario("Hola").build();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(repo.save(c)).thenReturn(c);
        ContactoResponse r = service.marcarLeido(1L);
        assertTrue(c.isLeido());
        assertTrue(r.isLeido());
        verify(repo).save(c);
    }

    @Test void marcarLeidoInexistenteLanzaExcepcion() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.marcarLeido(1L));
    }
}
