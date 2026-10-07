package com.huerto.hogar.frontend.config;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FrontendSession {

    private String token;
    private String correo;
    private String rol;
    private Long usuarioId;

    public boolean isLogged() {
        return token != null && !token.isBlank();
    }

    public void login(String token, String correo, String rol, Long usuarioId) {
        this.token = token;
        this.correo = correo;
        this.rol = rol;
        this.usuarioId = usuarioId;
    }

    public void logout() {
        token = null;
        correo = null;
        rol = null;
        usuarioId = null;
    }

    public String getToken() {
        return token;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public boolean isAdmin() {
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "ADMIN".equalsIgnoreCase(rol);
    }

    public boolean isVendedor() {
        return "VENDEDOR".equalsIgnoreCase(rol);
    }
}
