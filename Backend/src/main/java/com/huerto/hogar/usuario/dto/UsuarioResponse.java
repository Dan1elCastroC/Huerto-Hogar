package com.huerto.hogar.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Schema(description = "Datos públicos de un usuario")
@Data @Builder
public class UsuarioResponse {
    @Schema(description = "Identificador del usuario", example = "1")
    private Long id;
    @Schema(description = "RUN del usuario", example = "123456785")
    private String run;
    @Schema(description = "Nombres", example = "Juan")
    private String nombre;
    @Schema(description = "Apellidos", example = "Soto Rojas")
    private String apellidos;
    @Schema(description = "Correo electrónico", example = "juan@correo.cl")
    private String correo;
    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;
    @Schema(description = "Fecha de nacimiento", example = "1995-04-20")
    private LocalDate fechaNacimiento;
    @Schema(description = "Dirección", example = "Av. Providencia 1234")
    private String direccion;
    @Schema(description = "Región de residencia", example = "Metropolitana de Santiago")
    private String region;
    @Schema(description = "Comuna de residencia", example = "Providencia")
    private String comuna;
    @Schema(description = "Rol del usuario", example = "CLIENTE")
    private String rol;
    @Schema(description = "Indica si la cuenta está activa", example = "true")
    private boolean activo;
    @Schema(description = "Fecha y hora de creación de la cuenta", example = "2026-11-09T10:15:30")
    private LocalDateTime creadoEn;
}
