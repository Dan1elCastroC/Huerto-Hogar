package com.huerto.hogar;

import com.huerto.hogar.cupon.controller.CuponController;
import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.dto.CuponResponse;
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

    @Test void saveEntregaElDtoAlServicioYRetorna201() {
        CuponDto dto = dto(BigDecimal.TEN);
        when(service.save(any(CuponDto.class))).thenReturn(new CuponResponse());
        var respuesta = controller.save(dto);
        assertEquals(201, respuesta.getStatusCodeValue());
        assertTrue(respuesta.getBody() instanceof CuponResponse);
        org.mockito.Mockito.verify(service).save(dto);
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
        when(service.findAll()).thenReturn(java.util.List.of(new CuponResponse()));
        assertEquals(200, controller.findAll().getStatusCodeValue());
    }

    @Test void findAllConErrorRetorna400() {
        when(service.findAll()).thenThrow(new RuntimeException("mal"));
        assertEquals(400, controller.findAll().getStatusCodeValue());
    }

    @Test void validarCuponExistenteRetorna200() {
        when(service.findByCodigo("DESC10")).thenReturn(new CuponResponse());
        assertEquals(200, controller.validar("DESC10").getStatusCodeValue());
    }

    @Test void validarCuponInvalidoRetorna400ConMensaje() {
        when(service.findByCodigo("X")).thenThrow(new RuntimeException("Cupón expirado"));
        var r = controller.validar("X");
        assertEquals(400, r.getStatusCodeValue());
        assertEquals("Cupón expirado", r.getBody());
    }

    @Test void saveConErrorDelServicioRetorna400() {
        when(service.save(any(CuponDto.class))).thenThrow(new RuntimeException("Fecha inválida"));
        var r = controller.save(dto(BigDecimal.TEN));
        assertEquals(400, r.getStatusCodeValue());
        assertEquals("Fecha inválida", r.getBody());
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
