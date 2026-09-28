package com.huerto.hogar.auth;
import lombok.Data;
import javax.validation.constraints.*;
@Data
public class LoginRequest {
    @NotBlank @Email @Size(max=100) private String correo;
    @NotBlank @Size(min=4,max=10) private String contrasena;
}
