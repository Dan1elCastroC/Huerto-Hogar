package com.huerto.hogar.auth;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class LoginResponse {
    private String token;
    private String correo;
    private String rol;
    private Long usuarioId;
}
