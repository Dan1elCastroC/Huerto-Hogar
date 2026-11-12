package com.huerto.hogar;

import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.dto.CategoriaResponse;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.mapper.CategoriaMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** El mapper (MapStruct) convierte DTO -> entity al guardar y entity -> DTO al responder. */
class CategoriaMapperTest {
    private final CategoriaMapper mapper = Mappers.getMapper(CategoriaMapper.class);

    @Test void toEntityCopiaLosCamposDelDto() {
        CategoriaDto dto = new CategoriaDto();
        dto.setNombre("Frutas"); dto.setDescripcion("Frescas");
        CategoriaEntity e = mapper.toEntity(dto);
        assertEquals("Frutas", e.getNombre());
        assertEquals("Frescas", e.getDescripcion());
        assertNull(e.getId());
        assertTrue(e.isActivo());
    }

    @Test void toDtoCopiaLosCamposDeLaEntidad() {
        CategoriaEntity e = CategoriaEntity.builder().id(5L).nombre("Frutas").descripcion("Frescas").build();
        CategoriaResponse r = mapper.toDto(e);
        assertEquals(5L, r.getId());
        assertEquals("Frutas", r.getNombre());
        assertEquals("Frescas", r.getDescripcion());
        assertTrue(r.isActivo());
    }

    @Test void toDtoListConvierteCadaElemento() {
        List<CategoriaResponse> lista = mapper.toDtoList(List.of(new CategoriaEntity(), new CategoriaEntity()));
        assertEquals(2, lista.size());
    }

    @Test void conNullDevuelveNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDto(null));
        assertNull(mapper.toDtoList(null));
    }
}
