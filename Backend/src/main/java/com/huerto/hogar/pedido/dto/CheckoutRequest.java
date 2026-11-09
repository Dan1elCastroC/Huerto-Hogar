package com.huerto.hogar.pedido.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;

@Schema(description = "Datos para confirmar la compra")
@Data
public class CheckoutRequest {

    @Schema(description = "Dirección de entrega (máx. 300 caracteres)", example = "Av. Siempre Viva 742, Santiago")
    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 300)
    private String direccionEntrega;

    @Schema(description = "Fecha de entrega deseada yyyy-MM-dd (opcional)", example = "2026-12-01")
    private String fechaEntregaDeseada; // yyyy-MM-dd, opcional

    @Schema(description = "Código de cupón de descuento (opcional)", example = "DESC10")
    private String codigoCupon;         // opcional
}
