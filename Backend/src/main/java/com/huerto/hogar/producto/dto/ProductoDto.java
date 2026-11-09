package com.huerto.hogar.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
@Schema(description = "Datos para crear o actualizar un producto")
@Data
public class ProductoDto {
    @Schema(description = "Código único del producto (mínimo 3 caracteres)", example = "FR001")
    @NotBlank(message = "El código es obligatorio") @Size(min = 3, message = "Código mínimo 3 caracteres")
    private String codigo;
    @Schema(description = "Nombre del producto (máx. 100 caracteres)", example = "Manzana Fuji")
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 100)
    private String nombre;
    @Schema(description = "Descripción del producto (máx. 500 caracteres)", example = "Manzana dulce y crujiente")
    @Size(max = 500)
    private String descripcion;
    @Schema(description = "Precio unitario (mínimo 0)", example = "1200")
    @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.0", message = "Precio mínimo 0")
    private BigDecimal precio;
    @Schema(description = "Unidades disponibles (mínimo 0)", example = "50")
    @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "Stock mínimo 0")
    private Integer stock;
    @Schema(description = "Umbral de alerta de stock bajo (mínimo 0)", example = "10")
    @Min(value = 0, message = "Stock crítico mínimo 0")
    private Integer stockCritico;
    @Schema(description = "URL de la imagen del producto", example = "https://huertohogar.cl/img/manzana.jpg")
    private String imagenUrl;
    @Schema(description = "ID de la categoría a la que pertenece", example = "1")
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
}
