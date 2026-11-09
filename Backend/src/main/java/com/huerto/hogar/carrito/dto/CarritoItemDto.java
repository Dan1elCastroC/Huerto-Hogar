package com.huerto.hogar.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;

@Schema(description = "Producto y cantidad a agregar al carrito")
@Data
public class CarritoItemDto {
    @Schema(description = "ID del producto", example = "1")
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    @Schema(description = "Cantidad de unidades (mínimo 1)", example = "2")
    @NotNull @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
}
