package com.huerto.hogar;

import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.dto.CuponResponse;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.mapper.CuponMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** El mapper (MapStruct) convierte DTO -> entity al guardar y entity -> DTO al responder. */
class CuponMapperTest {
    private final CuponMapper mapper = Mappers.getMapper(CuponMapper.class);

    @Test void toEntityCopiaLosCamposDelDto() {
        CuponDto dto = new CuponDto();
        dto.setCodigo("DESC10"); dto.setPorcentaje(BigDecimal.TEN); dto.setFechaExpiracion("2030-01-31");
        CuponEntity e = mapper.toEntity(dto);
        assertEquals("DESC10", e.getCodigo());
        assertEquals(BigDecimal.TEN, e.getPorcentaje());
        assertEquals(LocalDate.of(2030, 1, 31), e.getFechaExpiracion());
        assertTrue(e.isActivo());
    }

    @Test void toDtoCopiaLosCamposDeLaEntidad() {
        CuponEntity e = CuponEntity.builder().id(9L).codigo("DESC10").porcentaje(new BigDecimal("12.50"))
            .fechaExpiracion(LocalDate.of(2031, 5, 6)).build();
        CuponResponse r = mapper.toDto(e);
        assertEquals(9L, r.getId());
        assertEquals("DESC10", r.getCodigo());
        assertEquals(new BigDecimal("12.50"), r.getPorcentaje());
        assertEquals(LocalDate.of(2031, 5, 6), r.getFechaExpiracion());
        assertTrue(r.isActivo());
    }

    @Test void toDtoListConvierteCadaElemento() {
        List<CuponResponse> lista = mapper.toDtoList(List.of(new CuponEntity(), new CuponEntity()));
        assertEquals(2, lista.size());
    }

    @Test void conNullDevuelveNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDto(null));
        assertNull(mapper.toDtoList(null));
    }

    @Test void fechaVaciaONulaSignificaSinVencimiento() {
        CuponDto dto = new CuponDto();
        dto.setCodigo("A"); dto.setPorcentaje(BigDecimal.ONE);
        dto.setFechaExpiracion(null);
        assertNull(mapper.toEntity(dto).getFechaExpiracion());
        dto.setFechaExpiracion("   ");
        assertNull(mapper.toEntity(dto).getFechaExpiracion());
    }

    @Test void fechaMalFormadaLanzaExcepcion() {
        CuponDto dto = new CuponDto();
        dto.setCodigo("A"); dto.setPorcentaje(BigDecimal.ONE); dto.setFechaExpiracion("31/01/2030");
        assertThrows(java.time.format.DateTimeParseException.class, () -> mapper.toEntity(dto));
    }
}
