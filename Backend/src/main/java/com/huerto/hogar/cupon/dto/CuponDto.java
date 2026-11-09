package com.huerto.hogar.cupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Datos para crear un cupón de descuento")
@Data
public class CuponDto {
    @Schema(description = "Código del cupón (máx. 30 caracteres)", example = "DESC10")
    @NotBlank(message = "El código es obligatorio") @Size(max = 30)
    private String codigo;
    @Schema(description = "Porcentaje de descuento entre 1 y 100", example = "10")
    @NotNull @DecimalMin("1") @DecimalMax("100")
    private BigDecimal porcentaje;
    @Schema(description = "Fecha de expiración en formato yyyy-MM-dd (opcional)", example = "2026-12-31")
    private String fechaExpiracion; // yyyy-MM-dd, opcional
}
