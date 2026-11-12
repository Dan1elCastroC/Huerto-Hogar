package com.huerto.hogar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.security.ErroresSeguridadHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class ErroresSeguridadHandlerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ErroresSeguridadHandler handler = new ErroresSeguridadHandler(mapper);

    @AfterEach void limpiar() { SecurityContextHolder.clearContext(); }

    @Test void sinAutenticarResponde401ConApiErrorJson() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        handler.commence(new MockHttpServletRequest(), res, new BadCredentialsException("x"));
        assertEquals(401, res.getStatus());
        assertTrue(res.getContentType().startsWith("application/json"));
        JsonNode json = mapper.readTree(res.getContentAsString());
        assertEquals(401, json.get("status").asInt());
        assertEquals("No autenticado", json.get("error").asText());
        assertEquals("Debes iniciar sesión para acceder a este recurso", json.get("mensaje").asText());
        assertTrue(json.has("timestamp"));
    }

    @Test void sinPermisoResponde403ConApiErrorJson() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        handler.handle(new MockHttpServletRequest(), res, new AccessDeniedException("x"));
        assertEquals(403, res.getStatus());
        JsonNode json = mapper.readTree(res.getContentAsString());
        assertEquals(403, json.get("status").asInt());
        assertEquals("Sin permiso", json.get("mensaje").asText());
    }

    @Test void globalHandlerResponde401SiElUsuarioEsAnonimo() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
            "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        var r = new GlobalExceptionHandler().forbidden(new AccessDeniedException("no"));
        assertEquals(401, r.getStatusCodeValue());
        assertEquals("No autenticado", r.getBody().getError());
    }

    @Test void globalHandlerResponde403SiElUsuarioEstaAutenticado() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
            "a@gmail.com", null, AuthorityUtils.createAuthorityList("ROLE_CLIENTE")));
        var r = new GlobalExceptionHandler().forbidden(new AccessDeniedException("no"));
        assertEquals(403, r.getStatusCodeValue());
        assertEquals("Sin permiso", r.getBody().getMensaje());
    }
}
