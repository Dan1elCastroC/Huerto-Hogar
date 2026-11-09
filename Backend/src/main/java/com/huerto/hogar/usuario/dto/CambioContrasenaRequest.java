package com.huerto.hogar.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
@Schema(description = "Datos para cambiar la contraseña propia")
@Data
public class CambioContrasenaRequest {
    @Schema(description = "Contraseña actual", example = "clave123")
    @NotBlank private String contrasenaActual;
    @Schema(description = "Nueva contraseña (4 a 10 caracteres)", example = "nueva123")
    @NotBlank @Size(min=4,max=10,message="Nueva contraseña entre 4 y 10 caracteres") private String nuevaContrasena;
    @Schema(description = "Repetición de la nueva contraseña", example = "nueva123")
    @NotBlank private String confirmarContrasena;
}
