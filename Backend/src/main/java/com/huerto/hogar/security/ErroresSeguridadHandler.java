package com.huerto.hogar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.hogar.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Respuestas JSON (con el mismo formato {@link ApiError} del resto de la API) para los fallos de la
 * cadena de seguridad: 401 cuando falta o es inválido el token JWT y 403 cuando el rol no alcanza.
 */
@Component
@RequiredArgsConstructor
public class ErroresSeguridadHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
        escribir(res, HttpStatus.UNAUTHORIZED, "No autenticado",
            "Debes iniciar sesión para acceder a este recurso");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        escribir(res, HttpStatus.FORBIDDEN, "Forbidden", "Sin permiso");
    }

    private void escribir(HttpServletResponse res, HttpStatus status, String error, String mensaje) throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        mapper.writeValue(res.getWriter(),
            ApiError.builder().status(status.value()).error(error).mensaje(mensaje).build());
    }
}
