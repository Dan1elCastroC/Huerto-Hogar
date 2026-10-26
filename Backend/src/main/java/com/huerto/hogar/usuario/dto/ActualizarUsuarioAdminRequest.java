package com.huerto.hogar.usuario.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

@Getter
@Setter
public class ActualizarUsuarioAdminRequest extends ActualizarUsuarioRequest {
    @NotBlank @Email @Size(max = 100)
    private String correo;
    private String rol;
    @Size(min = 4, max = 10)
    private String contrasena;
    @Size(min = 4, max = 10)
    private String confirmarContrasena;
}
