package com.huerto.hogar.pedido.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Pedido con sus detalles")
@Data @Builder
public class PedidoResponse {
    @Schema(description = "Identificador del pedido", example = "1")
    private Long id;
    @Schema(description = "ID del usuario que hizo el pedido", example = "1")
    private Long usuarioId;
    @Schema(description = "Estado del pedido (PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO)", example = "PENDIENTE")
    private String estado;
    @Schema(description = "Dirección de entrega", example = "Av. Siempre Viva 742, Santiago")
    private String direccionEntrega;
    @Schema(description = "Fecha de entrega deseada", example = "2026-12-01")
    private LocalDate fechaEntregaDeseada;
    @Schema(description = "Código del cupón aplicado, si existe", example = "DESC10")
    private String cuponAplicado;
    @Schema(description = "Subtotal del pedido", example = "15000")
    private BigDecimal subtotal;
    @Schema(description = "Monto descontado", example = "1500")
    private BigDecimal descuento;
    @Schema(description = "Total del pedido", example = "13500")
    private BigDecimal total;
    @Schema(description = "Fecha y hora de creación", example = "2026-11-09T10:15:30")
    private LocalDateTime creadoEn;
    @Schema(description = "Productos del pedido")
    private List<DetalleResponse> detalles;

    @Data @Builder
    public static class DetalleResponse {
    @Schema(description = "ID del producto", example = "1")
        private Long productoId;
    @Schema(description = "Nombre del producto", example = "Manzana Fuji")
        private String productoNombre;
    @Schema(description = "URL de la imagen del producto", example = "https://huertohogar.cl/img/manzana.jpg")
        private String productoImagenUrl;
    @Schema(description = "Unidades compradas", example = "2")
        private Integer cantidad;
    @Schema(description = "Precio por unidad al momento de la compra", example = "1200")
        private BigDecimal precioUnitario;
    @Schema(description = "Subtotal del pedido", example = "15000")
        private BigDecimal subtotal;
    }
}
