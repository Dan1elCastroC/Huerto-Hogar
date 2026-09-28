package com.huerto.hogar.producto.dto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
@Data @Builder
public class ProductoResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockCritico;
    private boolean stockCriticoAlerta;
    private String imagenUrl;
    private Long categoriaId;
    private String categoriaNombre;
    private boolean activo;
}
