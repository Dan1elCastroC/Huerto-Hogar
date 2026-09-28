package com.huerto.hogar.reporte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Se instancia desde la @Query JPQL (constructor expression)
@Data @AllArgsConstructor
public class ProductoVendidoDto {
    private Long productoId;
    private String nombre;
    private Long unidadesVendidas;
}
