package com.huerto.hogar;

import com.huerto.hogar.cupon.controller.CuponController;
import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.Validation;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuponControllerTest {
    @Mock ICuponService service;
    @InjectMocks CuponController controller;

    @Test void saveAceptaFechaVaciaComoSinVencimiento() {
        CuponDto dto = dto(BigDecimal.TEN);
        dto.setFechaExpiracion("  ");
        when(service.save(any(CuponEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        var respuesta = controller.save(dto);
        assertEquals(201, respuesta.getStatusCodeValue());
        assertNull(((CuponEntity) respuesta.getBody()).getFechaExpiracion());
    }

    @Test void porcentajeSoloAceptaValoresEntreUnoYCien() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            for (BigDecimal porcentaje : Arrays.asList(null, BigDecimal.ZERO,
                    new BigDecimal("-1"), new BigDecimal("100.01"))) {
                assertFalse(validator.validate(dto(porcentaje)).isEmpty(), "Debe rechazar: " + porcentaje);
            }
            assertTrue(validator.validate(dto(BigDecimal.ONE)).isEmpty());
            assertTrue(validator.validate(dto(new BigDecimal("100"))).isEmpty());
        }
    }

    @Test void findAllOk() {
        when(service.findAll()).thenReturn(java.util.List.of(new CuponEntity()));
        assertEquals(200, controller.findAll().getStatusCodeValue());
    }

    @Test void findAllConErrorRetorna400() {
        when(service.findAll()).thenThrow(new RuntimeException("mal"));
        assertEquals(400, controller.findAll().getStatusCodeValue());
    }

    @Test void validarCuponExistenteRetorna200() {
        when(service.findByCodigo("DESC10")).thenReturn(new CuponEntity());
        assertEquals(200, controller.validar("DESC10").getStatusCodeValue());
    }

    @Test void validarCuponInvalidoRetorna400ConMensaje() {
        when(service.findByCodigo("X")).thenThrow(new RuntimeException("Cupón expirado"));
        var r = controller.validar("X");
        assertEquals(400, r.getStatusCodeValue());
        assertEquals("Cupón expirado", r.getBody());
    }

    @Test void saveConFechaValidaLaParsea() {
        CuponDto dto = dto(BigDecimal.TEN);
        dto.setFechaExpiracion("2030-01-31");
        when(service.save(any(CuponEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        var r = controller.save(dto);
        assertEquals(java.time.LocalDate.of(2030, 1, 31), ((CuponEntity) r.getBody()).getFechaExpiracion());
    }

    @Test void saveConFechaMalFormadaRetorna400() {
        CuponDto dto = dto(BigDecimal.TEN);
        dto.setFechaExpiracion("31/01/2030");
        assertEquals(400, controller.save(dto).getStatusCodeValue());
    }

    @Test void deleteOkYDeleteInexistente() {
        assertEquals(200, controller.delete(1L).getStatusCodeValue());
        org.mockito.Mockito.doThrow(new RuntimeException("no")).when(service).deleteById(2L);
        assertEquals(404, controller.delete(2L).getStatusCodeValue());
    }

    private CuponDto dto(BigDecimal porcentaje) {
        CuponDto dto = new CuponDto();
        dto.setCodigo("DESC10");
        dto.setPorcentaje(porcentaje);
        return dto;
    }
}
