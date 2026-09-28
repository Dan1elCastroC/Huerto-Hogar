package com.huerto.hogar.pedido.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CheckoutRequest {

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 300)
    private String direccionEntrega;

    private String fechaEntregaDeseada; // yyyy-MM-dd, opcional

    private String codigoCupon;         // opcional
}
