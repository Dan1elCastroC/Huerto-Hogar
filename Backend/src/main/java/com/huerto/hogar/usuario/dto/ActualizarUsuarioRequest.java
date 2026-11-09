package com.huerto.hogar.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
@Schema(description = "Datos personales editables de un usuario")
@Data
public class ActualizarUsuarioRequest {
    @Schema(description = "Nombres (máx. 50 caracteres)", example = "Juan")
    @NotBlank @Size(max=50) private String nombre;
    @Schema(description = "Apellidos (máx. 100 caracteres)", example = "Soto Rojas")
    @NotBlank @Size(max=100) private String apellidos;
    @Schema(description = "Teléfono de contacto (opcional)", example = "+56912345678")
    private String telefono;
    @Schema(description = "Fecha de nacimiento yyyy-MM-dd (opcional)", example = "1995-04-20")
    private String fechaNacimiento;
    @Schema(description = "Dirección (máx. 300 caracteres)", example = "Av. Providencia 1234")
    @NotBlank @Size(max=300) private String direccion;
    @Schema(description = "Región de residencia", example = "Metropolitana de Santiago")
    @NotBlank private String region;
    @Schema(description = "Comuna de residencia", example = "Providencia")
    @NotBlank private String comuna;
}
