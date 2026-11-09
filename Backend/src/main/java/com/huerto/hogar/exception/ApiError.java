package com.huerto.hogar.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
@Schema(description = "Respuesta estándar de error de la API")
@Data @Builder
public class ApiError {
    @Schema(description = "Código HTTP del error", example = "400")
    private int status;
    @Schema(description = "Texto del estado HTTP", example = "Bad Request")
    private String error;
    @Schema(description = "Mensaje descriptivo del error", example = "Datos de entrada inválidos")
    private String mensaje;
    @Schema(description = "Lista de errores de validación por campo")
    private List<String> detalles;
    @Schema(description = "Fecha y hora en que ocurrió el error", example = "2026-11-09T10:15:30")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
