package com.huerto.hogar.categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
@Schema(description = "Datos de una categoría de productos")
@Data
public class CategoriaDto {
    @Schema(description = "Nombre de la categoría (máx. 80 caracteres)", example = "Frutas Frescas")
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 80)
    private String nombre;
    @Schema(description = "Descripción de la categoría (máx. 300 caracteres)", example = "Frutas de temporada cultivadas localmente")
    @Size(max = 300)
    private String descripcion;
}
