package com.huerto.hogar;

import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.dto.ContactoResponse;
import com.huerto.hogar.contacto.entity.ContactoEntity;
import com.huerto.hogar.contacto.mapper.ContactoMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** El mapper (MapStruct) convierte DTO -> entity al guardar y entity -> DTO al responder. */
class ContactoMapperTest {
    private final ContactoMapper mapper = Mappers.getMapper(ContactoMapper.class);

    @Test void toEntityCopiaLosCamposDelDto() {
        ContactoDto dto = new ContactoDto();
        dto.setNombre("Ana"); dto.setCorreo("ana@gmail.com"); dto.setComentario("Hola");
        ContactoEntity e = mapper.toEntity(dto);
        assertEquals("Ana", e.getNombre());
        assertEquals("ana@gmail.com", e.getCorreo());
        assertEquals("Hola", e.getComentario());
        assertNull(e.getId());
        assertFalse(e.isLeido());
        assertNotNull(e.getCreadoEn());
    }

    @Test void toDtoCopiaLosCamposDeLaEntidad() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 9, 10, 15);
        ContactoEntity e = ContactoEntity.builder().id(7L).nombre("Ana").correo("ana@gmail.com")
            .comentario("Hola").leido(true).creadoEn(fecha).build();
        ContactoResponse r = mapper.toDto(e);
        assertEquals(7L, r.getId());
        assertEquals("Ana", r.getNombre());
        assertEquals("ana@gmail.com", r.getCorreo());
        assertEquals("Hola", r.getComentario());
        assertTrue(r.isLeido());
        assertEquals(fecha, r.getCreadoEn());
    }

    @Test void toDtoListConvierteCadaElemento() {
        List<ContactoResponse> lista = mapper.toDtoList(List.of(new ContactoEntity(), new ContactoEntity()));
        assertEquals(2, lista.size());
    }

    @Test void conNullDevuelveNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDto(null));
        assertNull(mapper.toDtoList(null));
    }
}
