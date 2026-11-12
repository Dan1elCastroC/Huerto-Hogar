package com.huerto.hogar.cupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Cupón de descuento")
@Data
public class CuponResponse {
    @Schema(description = "Identificador del cupón", example = "1")
    private Long id;
    @Schema(description = "Código del cupón", example = "DESC10")
    private String codigo;
    @Schema(description = "Porcentaje de descuento entre 1 y 100", example = "10")
    private BigDecimal porcentaje;
    @Schema(description = "Fecha de expiración (sin valor si no vence)", example = "2026-12-31")
    private LocalDate fechaExpiracion;
    @Schema(description = "Indica si el cupón está activo", example = "true")
    private boolean activo;
}
