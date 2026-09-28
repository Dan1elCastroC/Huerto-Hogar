package com.huerto.hogar.carrito.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CarritoItemDto {
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    @NotNull @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
}
