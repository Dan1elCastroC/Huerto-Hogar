package com.huerto.hogar.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
@Schema(description = "Datos para registrar un usuario")
@Data
public class RegistroRequest {
    @Schema(description = "RUN sin puntos ni guion, con dígito verificador (7 a 9 caracteres)", example = "123456785")
    @NotBlank(message = "El RUN es obligatorio") @Size(min=7,max=9,message="RUN entre 7 y 9 caracteres")
    private String run;
    @Schema(description = "Nombres (máx. 50 caracteres)", example = "Juan")
    @NotBlank(message = "Nombre obligatorio") @Size(max=50)
    private String nombre;
    @Schema(description = "Apellidos (máx. 100 caracteres)", example = "Soto Rojas")
    @NotBlank(message = "Apellidos obligatorios") @Size(max=100)
    private String apellidos;
    @Schema(description = "Correo electrónico (único)", example = "juan@correo.cl")
    @NotBlank(message = "Correo obligatorio") @Email(message = "Formato de correo inválido") @Size(max=100)
    private String correo;
    @Schema(description = "Contraseña (4 a 10 caracteres)", example = "clave123")
    @NotBlank(message = "Contraseña obligatoria") @Size(min=4,max=10,message="Contraseña entre 4 y 10 caracteres")
    private String contrasena;
    @Schema(description = "Repetición de la contraseña", example = "clave123")
    @NotBlank(message = "Confirmar contraseña obligatorio")
    private String confirmarContrasena;
    @Schema(description = "Teléfono de contacto (opcional)", example = "+56912345678")
    private String telefono;
    @Schema(description = "Fecha de nacimiento yyyy-MM-dd (opcional)", example = "1995-04-20")
    private String fechaNacimiento;
    @Schema(description = "Dirección (máx. 300 caracteres)", example = "Av. Providencia 1234")
    @NotBlank(message = "Dirección obligatoria") @Size(max=300)
    private String direccion;
    @Schema(description = "Región de residencia", example = "Metropolitana de Santiago")
    @NotBlank(message = "Región obligatoria")
    private String region;
    @Schema(description = "Comuna de residencia", example = "Providencia")
    @NotBlank(message = "Comuna obligatoria")
    private String comuna;
    @Schema(description = "Rol (solo lo respeta el alta por administrador): ADMINISTRADOR, VENDEDOR o CLIENTE", example = "CLIENTE")
    private String rol;
}
