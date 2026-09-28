package com.huerto.hogar.pedido.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class PedidoResponse {
    private Long id;
    private String estado;
    private String direccionEntrega;
    private LocalDate fechaEntregaDeseada;
    private String cuponAplicado;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private LocalDateTime creadoEn;
    private List<DetalleResponse> detalles;

    @Data @Builder
    public static class DetalleResponse {
        private Long productoId;
        private String productoNombre;
        private String productoImagenUrl;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
