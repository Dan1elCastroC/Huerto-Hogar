package com.huerto.hogar.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
@Schema(description = "Credenciales de inicio de sesión")
@Data
public class LoginRequest {
    @Schema(description = "Correo electrónico del usuario", example = "cliente@huertohogar.cl")
    @NotBlank @Email @Size(max=100) private String correo;
    @Schema(description = "Contraseña del usuario (4 a 10 caracteres)", example = "clave123")
    @NotBlank @Size(min=4,max=10) private String contrasena;
}
