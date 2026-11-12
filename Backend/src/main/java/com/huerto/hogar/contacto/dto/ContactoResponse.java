package com.huerto.hogar.contacto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Mensaje de contacto recibido")
@Data
public class ContactoResponse {
    @Schema(description = "Identificador del mensaje", example = "1")
    private Long id;
    @Schema(description = "Nombre de quien escribe", example = "María Pérez")
    private String nombre;
    @Schema(description = "Correo de contacto", example = "maria@gmail.com")
    private String correo;
    @Schema(description = "Mensaje o consulta", example = "Quisiera información sobre despachos.")
    private String comentario;
    @Schema(description = "Indica si un administrador ya lo marcó como leído", example = "false")
    private boolean leido;
    @Schema(description = "Fecha y hora de recepción", example = "2026-11-09T10:15:30")
    private LocalDateTime creadoEn;
}
