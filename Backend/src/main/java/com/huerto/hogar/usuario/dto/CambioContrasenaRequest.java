package com.huerto.hogar.usuario.dto;
import lombok.Data;
import javax.validation.constraints.*;
@Data
public class CambioContrasenaRequest {
    @NotBlank private String contrasenaActual;
    @NotBlank @Size(min=4,max=10,message="Nueva contraseña entre 4 y 10 caracteres") private String nuevaContrasena;
    @NotBlank private String confirmarContrasena;
}
