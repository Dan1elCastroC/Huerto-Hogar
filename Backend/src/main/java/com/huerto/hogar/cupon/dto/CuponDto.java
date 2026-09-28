package com.huerto.hogar.cupon.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class CuponDto {
    @NotBlank(message = "El código es obligatorio") @Size(max = 30)
    private String codigo;
    @NotNull @DecimalMin("1") @DecimalMax("100")
    private BigDecimal porcentaje;
    private String fechaExpiracion; // yyyy-MM-dd, opcional
}
