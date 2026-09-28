package com.huerto.hogar.carrito.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class CarritoResponse {
    private List<ItemResponse> items;
    private int totalItems;
    private BigDecimal subtotal;
    private BigDecimal descuento;       // monto descontado si hay cupón
    private BigDecimal total;
    private String cuponAplicado;

    @Data @Builder
    public static class ItemResponse {
        private Long id;
        private Long productoId;
        private String productoNombre;
        private String productoImagenUrl;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
