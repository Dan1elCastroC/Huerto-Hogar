package com.huerto.hogar.categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Categoría de productos")
@Data
public class CategoriaResponse {
    @Schema(description = "Identificador de la categoría", example = "1")
    private Long id;
    @Schema(description = "Nombre de la categoría", example = "Frutas Frescas")
    private String nombre;
    @Schema(description = "Descripción de la categoría", example = "Frutas de temporada cultivadas localmente")
    private String descripcion;
    @Schema(description = "Indica si la categoría está activa", example = "true")
    private boolean activo;
}
