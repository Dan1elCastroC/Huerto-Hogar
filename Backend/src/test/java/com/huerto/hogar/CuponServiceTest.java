package com.huerto.hogar;

import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.repository.CuponRepository;
import com.huerto.hogar.cupon.service.CuponService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponServiceTest {
    @Mock CuponRepository repo;
    @InjectMocks CuponService service;

    @Test void findByIdInexistenteLanzaExcepcion() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(1L));
    }

    @Test void findByCodigoAceptaCodigoActivoNoExpirado() {
        CuponEntity c = CuponEntity.builder().codigo("DESC10").porcentaje(new BigDecimal("10")).fechaExpiracion(LocalDate.now().plusDays(1)).activo(true).build();
        when(repo.findByCodigoAndActivoTrue("DESC10")).thenReturn(Optional.of(c));
        assertSame(c, service.findByCodigo("desc10"));
    }

    @Test void findByCodigoRechazaCuponExpirado() {
        CuponEntity c = CuponEntity.builder().codigo("DESC10").porcentaje(new BigDecimal("10")).fechaExpiracion(LocalDate.now().minusDays(1)).activo(true).build();
        when(repo.findByCodigoAndActivoTrue("DESC10")).thenReturn(Optional.of(c));
        assertThrows(ReglaDeNegocioException.class, () -> service.findByCodigo("desc10"));
    }

    @Test void saveConvierteCodigoAMayusculas() {
        CuponEntity c = CuponEntity.builder().codigo("desc10").porcentaje(new BigDecimal("10")).build();
        when(repo.existsByCodigo("DESC10")).thenReturn(false);
        when(repo.save(c)).thenReturn(c);
        service.save(c);
        assertEquals("DESC10", c.getCodigo());
        verify(repo).save(c);
    }

    @Test void saveNoPermiteCodigoRepetido() {
        CuponEntity c = CuponEntity.builder().codigo("DESC10").porcentaje(new BigDecimal("10")).build();
        when(repo.existsByCodigo("DESC10")).thenReturn(true);
        assertThrows(ReglaDeNegocioException.class, () -> service.save(c));
        verify(repo, never()).save(any());
    }
}
