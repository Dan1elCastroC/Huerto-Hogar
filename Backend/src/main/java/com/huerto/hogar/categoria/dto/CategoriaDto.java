package com.huerto.hogar.categoria.dto;
import lombok.Data;
import javax.validation.constraints.*;
@Data
public class CategoriaDto {
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 80)
    private String nombre;
    @Size(max = 300)
    private String descripcion;
}
