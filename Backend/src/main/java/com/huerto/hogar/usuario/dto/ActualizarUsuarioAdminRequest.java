package com.huerto.hogar.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

@Schema(description = "Datos para que un administrador actualice un usuario")
@Getter
@Setter
public class ActualizarUsuarioAdminRequest extends ActualizarUsuarioRequest {
    @Schema(description = "Correo electrónico del usuario", example = "cliente@huertohogar.cl")
    @NotBlank @Email @Size(max = 100)
    private String correo;
    @Schema(description = "Rol: ADMINISTRADOR, VENDEDOR o CLIENTE", example = "CLIENTE")
    private String rol;
    @Schema(description = "Nueva contraseña (opcional, 4 a 10 caracteres)", example = "nueva123")
    @Size(min = 4, max = 10)
    private String contrasena;
    @Schema(description = "Repetición de la nueva contraseña", example = "nueva123")
    @Size(min = 4, max = 10)
    private String confirmarContrasena;
}
