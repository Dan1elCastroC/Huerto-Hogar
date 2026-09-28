package com.huerto.hogar.producto.dto;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
@Data
public class ProductoDto {
    @NotBlank(message = "El código es obligatorio") @Size(min = 3, message = "Código mínimo 3 caracteres")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 100)
    private String nombre;
    @Size(max = 500)
    private String descripcion;
    @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.0", message = "Precio mínimo 0")
    private BigDecimal precio;
    @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "Stock mínimo 0")
    private Integer stock;
    @Min(value = 0, message = "Stock crítico mínimo 0")
    private Integer stockCritico;
    private String imagenUrl;
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
}
