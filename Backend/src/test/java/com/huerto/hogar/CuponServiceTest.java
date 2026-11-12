package com.huerto.hogar;

import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.dto.CuponResponse;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.mapper.CuponMapper;
import com.huerto.hogar.cupon.repository.CuponRepository;
import com.huerto.hogar.cupon.service.CuponService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponServiceTest {
    @Mock CuponRepository repo;
    // Mapper real (no un spy): así el código generado por MapStruct también cuenta en JaCoCo.
    final CuponMapper mapper = Mappers.getMapper(CuponMapper.class);
    CuponService service;

    @BeforeEach void preparar() { service = new CuponService(repo, mapper); }

    private CuponDto dto(String codigo, String fecha) {
        CuponDto d = new CuponDto();
        d.setCodigo(codigo); d.setPorcentaje(BigDecimal.TEN); d.setFechaExpiracion(fecha);
        return d;
    }

    @Test void findByIdInexistenteLanzaExcepcion() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(1L));
    }

    @Test void findByIdDevuelveDto() {
        when(repo.findById(1L)).thenReturn(Optional.of(CuponEntity.builder().id(1L).codigo("A").porcentaje(BigDecimal.TEN).build()));
        CuponResponse r = service.findById(1L);
        assertEquals(1L, r.getId());
        assertEquals("A", r.getCodigo());
    }

    @Test void findAllConvierteLasEntidadesEnDtos() {
        when(repo.findAll()).thenReturn(List.of(CuponEntity.builder().codigo("A").porcentaje(BigDecimal.TEN).build()));
        assertEquals("A", service.findAll().get(0).getCodigo());
    }

    @Test void findByCodigoAceptaCodigoActivoNoExpirado() {
        CuponEntity c = CuponEntity.builder().codigo("DESC10").porcentaje(new BigDecimal("10")).fechaExpiracion(LocalDate.now().plusDays(1)).activo(true).build();
        when(repo.findByCodigoAndActivoTrue("DESC10")).thenReturn(Optional.of(c));
        CuponResponse r = service.findByCodigo("desc10");
        assertEquals("DESC10", r.getCodigo());
        assertEquals(new BigDecimal("10"), r.getPorcentaje());
    }

    @Test void findByCodigoRechazaCuponExpirado() {
        CuponEntity c = CuponEntity.builder().codigo("DESC10").porcentaje(new BigDecimal("10")).fechaExpiracion(LocalDate.now().minusDays(1)).activo(true).build();
        when(repo.findByCodigoAndActivoTrue("DESC10")).thenReturn(Optional.of(c));
        assertThrows(ReglaDeNegocioException.class, () -> service.findByCodigo("desc10"));
    }

    @Test void saveConvierteCodigoAMayusculas() {
        when(repo.existsByCodigo("DESC10")).thenReturn(false);
        when(repo.save(any(CuponEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        CuponResponse r = service.save(dto("desc10", null));
        ArgumentCaptor<CuponEntity> cap = ArgumentCaptor.forClass(CuponEntity.class);
        verify(repo).save(cap.capture());
        assertEquals("DESC10", cap.getValue().getCodigo());
        assertEquals("DESC10", r.getCodigo());
    }

    @Test void saveNoPermiteCodigoRepetido() {
        when(repo.existsByCodigo("DESC10")).thenReturn(true);
        assertThrows(ReglaDeNegocioException.class, () -> service.save(dto("DESC10", null)));
        verify(repo, never()).save(any());
    }

    @Test void saveConvierteLaFechaDelDto() {
        when(repo.save(any(CuponEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(LocalDate.of(2030, 1, 31), service.save(dto("F1", "2030-01-31")).getFechaExpiracion());
    }

    @Test void saveAceptaFechaVaciaComoSinVencimiento() {
        when(repo.save(any(CuponEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNull(service.save(dto("F2", "  ")).getFechaExpiracion());
    }

    @Test void saveConFechaMalFormadaLanzaExcepcionYNoGuarda() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> service.save(dto("F3", "31/01/2030")));
        verify(repo, never()).save(any());
    }

    @Test void findByCodigoAceptaFechaDeHoy() {
        CuponEntity c = CuponEntity.builder().codigo("HOY").porcentaje(BigDecimal.TEN)
            .fechaExpiracion(LocalDate.now()).build();
        when(repo.findByCodigoAndActivoTrue("HOY")).thenReturn(Optional.of(c));
        assertEquals("HOY", service.findByCodigo("hoy").getCodigo());
    }

    @Test void findByCodigoAceptaSinVencimiento() {
        CuponEntity c = CuponEntity.builder().codigo("SIEMPRE").porcentaje(BigDecimal.TEN).build();
        when(repo.findByCodigoAndActivoTrue("SIEMPRE")).thenReturn(Optional.of(c));
        assertNull(service.findByCodigo("siempre").getFechaExpiracion());
    }

    @Test void findByCodigoRechazaInexistenteOInactivo() {
        when(repo.findByCodigoAndActivoTrue("INACTIVO")).thenReturn(Optional.empty());
        when(repo.findByCodigoAndActivoTrue("NOEXISTE")).thenReturn(Optional.empty());
        assertAll(
            () -> assertThrows(ReglaDeNegocioException.class, () -> service.findByCodigo("INACTIVO")),
            () -> assertThrows(ReglaDeNegocioException.class, () -> service.findByCodigo("NOEXISTE"))
        );
    }

    @Test void findByCodigoIgnoraEspaciosExteriores() {
        CuponEntity c = CuponEntity.builder().codigo("DESC10").porcentaje(BigDecimal.TEN).build();
        when(repo.findByCodigoAndActivoTrue(anyString())).thenAnswer(inv ->
            "DESC10".equals(inv.getArgument(0)) ? Optional.of(c) : Optional.empty());
        assertEquals("DESC10", service.findByCodigo("  desc10  ").getCodigo());
    }

    @Test void saveNormalizaEspaciosYMayusculas() {
        when(repo.save(any(CuponEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("DESC10", service.save(dto("  desc10  ", null)).getCodigo());
    }

    @Test void saveNoDuplicaCodigoConEspacios() {
        when(repo.existsByCodigo(anyString())).thenAnswer(inv -> "DESC10".equals(inv.getArgument(0)));
        assertThrows(ReglaDeNegocioException.class, () -> service.save(dto("  desc10  ", null)));
        verify(repo, never()).save(any());
    }

    @Test void updateCambiaPorcentajeYFechaSinTocarElCodigo() {
        CuponEntity c = CuponEntity.builder().id(1L).codigo("DESC10").porcentaje(BigDecimal.TEN).activo(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(repo.save(c)).thenReturn(c);
        CuponDto nuevo = dto("IGNORADO", "2031-02-01");
        nuevo.setPorcentaje(new BigDecimal("25"));
        CuponResponse r = service.update(1L, nuevo);
        assertEquals("DESC10", c.getCodigo());
        assertEquals(new BigDecimal("25"), r.getPorcentaje());
        assertEquals(LocalDate.of(2031, 2, 1), r.getFechaExpiracion());
        assertTrue(c.isActivo());
    }

    @Test void deleteDesactivaElCupon() {
        CuponEntity c = CuponEntity.builder().id(1L).codigo("DESC10").porcentaje(BigDecimal.TEN).activo(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        service.deleteById(1L);
        assertFalse(c.isActivo());
        verify(repo).save(c);
    }
}
