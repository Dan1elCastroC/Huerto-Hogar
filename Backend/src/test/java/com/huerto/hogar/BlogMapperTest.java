package com.huerto.hogar;

import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.dto.BlogResponse;
import com.huerto.hogar.blog.entity.BlogEntity;
import com.huerto.hogar.blog.mapper.BlogMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** El mapper (MapStruct) convierte DTO -> entity al guardar y entity -> DTO al responder. */
class BlogMapperTest {
    private final BlogMapper mapper = Mappers.getMapper(BlogMapper.class);

    @Test void toEntityCopiaLosCamposDelDto() {
        BlogDto dto = new BlogDto();
        dto.setTitulo("T"); dto.setDescripcion("D"); dto.setImagenUrl("http://i/x.jpg");
        BlogEntity e = mapper.toEntity(dto);
        assertEquals("T", e.getTitulo());
        assertEquals("D", e.getDescripcion());
        assertEquals("http://i/x.jpg", e.getImagenUrl());
        assertTrue(e.isActivo()); // un activo nulo conserva el valor por defecto
        dto.setActivo(false);
        assertFalse(mapper.toEntity(dto).isActivo());
    }

    @Test void toDtoCopiaLosCamposDeLaEntidad() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 2, 3, 4);
        BlogEntity e = BlogEntity.builder().id(3L).titulo("T").descripcion("D").imagenUrl("http://i/x.jpg")
            .activo(false).creadoEn(fecha).build();
        BlogResponse r = mapper.toDto(e);
        assertEquals(3L, r.getId());
        assertEquals("T", r.getTitulo());
        assertEquals("D", r.getDescripcion());
        assertEquals("http://i/x.jpg", r.getImagenUrl());
        assertFalse(r.isActivo());
        assertEquals(fecha, r.getCreadoEn());
    }

    @Test void toDtoListConvierteCadaElemento() {
        List<BlogResponse> lista = mapper.toDtoList(List.of(new BlogEntity(), new BlogEntity()));
        assertEquals(2, lista.size());
    }

    @Test void conNullDevuelveNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDto(null));
        assertNull(mapper.toDtoList(null));
    }
}
