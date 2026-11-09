package com.huerto.hogar.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
@Schema(description = "Respuesta de un inicio de sesión exitoso")
@Data @AllArgsConstructor
public class LoginResponse {
    @Schema(description = "Token JWT para usar en el encabezado Authorization: Bearer", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGh1ZXJ0b2hvZ2FyLmNsIn0.firma")
    private String token;
    @Schema(description = "Correo del usuario autenticado", example = "cliente@huertohogar.cl")
    private String correo;
    @Schema(description = "Rol del usuario (ADMINISTRADOR, VENDEDOR o CLIENTE)", example = "CLIENTE")
    private String rol;
    @Schema(description = "Identificador del usuario", example = "1")
    private Long usuarioId;
}
