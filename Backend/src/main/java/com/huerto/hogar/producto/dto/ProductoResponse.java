package com.huerto.hogar.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
@Schema(description = "Producto del catálogo")
@Data @Builder
public class ProductoResponse {
    @Schema(description = "Identificador del producto", example = "1")
    private Long id;
    @Schema(description = "Código único del producto", example = "FR001")
    private String codigo;
    @Schema(description = "Nombre del producto", example = "Manzana Fuji")
    private String nombre;
    @Schema(description = "Descripción del producto", example = "Manzana dulce y crujiente")
    private String descripcion;
    @Schema(description = "Precio unitario", example = "1200")
    private BigDecimal precio;
    @Schema(description = "Unidades disponibles", example = "50")
    private Integer stock;
    @Schema(description = "Umbral de alerta de stock bajo", example = "10")
    private Integer stockCritico;
    @Schema(description = "true si el stock está en o bajo el umbral crítico", example = "false")
    private boolean stockCriticoAlerta;
    @Schema(description = "URL de la imagen del producto", example = "https://huertohogar.cl/img/manzana.jpg")
    private String imagenUrl;
    @Schema(description = "ID de la categoría", example = "1")
    private Long categoriaId;
    @Schema(description = "Nombre de la categoría", example = "Frutas Frescas")
    private String categoriaNombre;
    @Schema(description = "Indica si el producto está activo", example = "true")
    private boolean activo;
}
