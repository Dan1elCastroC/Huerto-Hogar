package com.huerto.hogar.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Carrito de compras con totales")
@Data @Builder
public class CarritoResponse {
    @Schema(description = "Productos del carrito")
    private List<ItemResponse> items;
    @Schema(description = "Cantidad total de unidades", example = "3")
    private int totalItems;
    @Schema(description = "Suma de los productos antes de descuento", example = "15000")
    private BigDecimal subtotal;
    @Schema(description = "Monto descontado si se aplicó un cupón", example = "1500")
    private BigDecimal descuento;       // monto descontado si hay cupón
    @Schema(description = "Total a pagar", example = "13500")
    private BigDecimal total;
    @Schema(description = "Código del cupón aplicado, si existe", example = "DESC10")
    private String cuponAplicado;

    @Data @Builder
    public static class ItemResponse {
    @Schema(description = "Identificador del ítem de carrito", example = "10")
        private Long id;
    @Schema(description = "ID del producto", example = "1")
        private Long productoId;
    @Schema(description = "Nombre del producto", example = "Manzana Fuji")
        private String productoNombre;
    @Schema(description = "URL de la imagen del producto", example = "https://huertohogar.cl/img/manzana.jpg")
        private String productoImagenUrl;
    @Schema(description = "Unidades del producto", example = "2")
        private Integer cantidad;
    @Schema(description = "Precio por unidad", example = "1200")
        private BigDecimal precioUnitario;
    @Schema(description = "Suma de los productos antes de descuento", example = "15000")
        private BigDecimal subtotal;
    }
}
