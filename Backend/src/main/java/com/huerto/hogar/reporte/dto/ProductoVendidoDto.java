package com.huerto.hogar.reporte.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

// Se instancia desde la @Query JPQL (constructor expression)
@Schema(description = "Producto con su total de unidades vendidas")
@Data @AllArgsConstructor
public class ProductoVendidoDto {
    @Schema(description = "ID del producto", example = "1")
    private Long productoId;
    @Schema(description = "Nombre del producto", example = "Manzana Fuji")
    private String nombre;
    @Schema(description = "Total de unidades vendidas", example = "120")
    private Long unidadesVendidas;
}
