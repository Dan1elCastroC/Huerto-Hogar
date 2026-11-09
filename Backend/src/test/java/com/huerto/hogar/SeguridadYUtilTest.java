package com.huerto.hogar;

import com.huerto.hogar.exception.ApiError;
import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import com.huerto.hogar.security.filter.JwtAuthFilter;
import com.huerto.hogar.security.jwt.JwtUtil;
import com.huerto.hogar.util.ValidadorRun;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.FilterChain;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SeguridadYUtilTest {
    static final String SECRETO = "HuertoHogar2025_SuperSecretKey_32Chars!!";

    // ── JwtUtil ──
    @Nested
    class JwtUtilTest {
        final JwtUtil jwt = new JwtUtil(SECRETO, 60_000);

        @Test void tokenGeneradoEsValidoYConservaCorreoYRol() {
            String token = jwt.generarToken("ana@gmail.com", "ADMINISTRADOR");
            assertTrue(jwt.esValido(token));
            assertEquals("ana@gmail.com", jwt.extraerCorreo(token));
            assertEquals("ADMINISTRADOR", jwt.extraerRol(token));
        }

        @Test void tokenManipuladoNoEsValido() {
            String token = jwt.generarToken("ana@gmail.com", "CLIENTE");
            assertFalse(jwt.esValido(token + "x"));
        }

        @Test void tokenFirmadoConOtraClaveNoEsValido() {
            JwtUtil otro = new JwtUtil("otra_clave_secreta_de_al_menos_32_caracteres_xx", 60_000);
            assertFalse(jwt.esValido(otro.generarToken("ana@gmail.com", "CLIENTE")));
        }

        @Test void tokenExpiradoNoEsValido() {
            JwtUtil corto = new JwtUtil(SECRETO, -1_000);
            assertFalse(jwt.esValido(corto.generarToken("ana@gmail.com", "CLIENTE")));
        }

        @Test void textoVacioONuloNoEsValido() {
            assertFalse(jwt.esValido(""));
            assertFalse(jwt.esValido(null));
            assertFalse(jwt.esValido("no.es.un.jwt"));
        }

        @Test void extraerDeTokenInvalidoLanzaExcepcion() {
            assertThrows(RuntimeException.class, () -> jwt.extraerCorreo("basura"));
        }

        @Test void tokenTieneFechaDeExpiracionFutura() {
            String token = jwt.generarToken("ana@gmail.com", "CLIENTE");
            assertTrue(new Date().before(new Date(System.currentTimeMillis() + 60_000)));
            assertTrue(jwt.esValido(token));
        }
    }

    // ── ValidadorRun ──
    @Nested
    class ValidadorRunTest {
        @Test void runConDigitoVerificadorCorrectoEsValido() {
            assertTrue(ValidadorRun.esValido("123456785"));
            assertTrue(ValidadorRun.esValido("190110222"));
        }

        @Test void aceptaKMinusculaYEspacios() {
            assertTrue(ValidadorRun.esValido(" 10000013k "));
        }

        @Test void digitoVerificadorCeroSeCalculaCorrectamente() {
            assertTrue(ValidadorRun.esValido("100000040"));
        }

        @Test void digitoVerificadorIncorrectoEsInvalido() {
            assertFalse(ValidadorRun.esValido("123456780"));
        }

        @Test void formatosInvalidos() {
            assertFalse(ValidadorRun.esValido(null));
            assertFalse(ValidadorRun.esValido(""));
            assertFalse(ValidadorRun.esValido("12.345.678-5"));
            assertFalse(ValidadorRun.esValido("abc"));
            assertFalse(ValidadorRun.esValido("123456"));
            assertFalse(ValidadorRun.esValido("1234567890"));
        }
    }

    // ── GlobalExceptionHandler ──
    @Nested
    class GlobalExceptionHandlerTest {
        final GlobalExceptionHandler handler = new GlobalExceptionHandler();

        @Test void recursoNoEncontradoDevuelve404() {
            ResponseEntity<ApiError> r = handler.notFound(new RecursoNoEncontradoException("No existe"));
            assertEquals(404, r.getStatusCodeValue());
            assertEquals("No existe", r.getBody().getMensaje());
            assertEquals("Not Found", r.getBody().getError());
            assertNotNull(r.getBody().getTimestamp());
        }

        @Test void reglaDeNegocioDevuelve400() {
            ResponseEntity<ApiError> r = handler.badRequest(new ReglaDeNegocioException("Regla rota"));
            assertEquals(400, r.getStatusCodeValue());
            assertEquals("Regla rota", r.getBody().getMensaje());
        }

        @Test void validacionDevuelve400ConDetallesPorCampo() throws Exception {
            BeanPropertyBindingResult br = new BeanPropertyBindingResult(new Object(), "dto");
            br.rejectValue(null, "x", "general"); // error global, no de campo
            br.addError(new org.springframework.validation.FieldError("dto", "nombre", "El nombre es obligatorio"));
            MethodParameter mp = new MethodParameter(Object.class.getMethod("toString"), -1);
            ResponseEntity<ApiError> r = handler.validacion(new MethodArgumentNotValidException(mp, br));
            assertEquals(400, r.getStatusCodeValue());
            assertEquals("Datos invalidos", r.getBody().getMensaje());
            assertEquals(java.util.List.of("El nombre es obligatorio"), r.getBody().getDetalles());
        }

        @Test void accesoDenegadoDevuelve403() {
            ResponseEntity<ApiError> r = handler.forbidden(new AccessDeniedException("no"));
            assertEquals(403, r.getStatusCodeValue());
            assertEquals("Sin permiso", r.getBody().getMensaje());
        }

        @Test void errorInesperadoDevuelve500() {
            ResponseEntity<ApiError> r = handler.general(new IllegalStateException("boom"));
            assertEquals(500, r.getStatusCodeValue());
            assertEquals("boom", r.getBody().getMensaje());
        }
    }

    // ── JwtAuthFilter ──
    @Nested
    @ExtendWith(MockitoExtension.class)
    class JwtAuthFilterTest {
        @Mock JwtUtil jwtUtil;
        @Mock FilterChain chain;

        @AfterEach void limpiar() { SecurityContextHolder.clearContext(); }

        private void ejecutar(String header) throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest();
            if (header != null) req.addHeader("Authorization", header);
            new JwtAuthFilter(jwtUtil).doFilter(req, new MockHttpServletResponse(), chain);
        }

        @Test void tokenValidoAutenticaConElRolDelToken() throws Exception {
            when(jwtUtil.esValido("abc")).thenReturn(true);
            when(jwtUtil.extraerCorreo("abc")).thenReturn("ana@gmail.com");
            when(jwtUtil.extraerRol("abc")).thenReturn("VENDEDOR");
            ejecutar("Bearer abc");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("ana@gmail.com", auth.getName());
            assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_VENDEDOR")));
            verify(chain).doFilter(any(), any());
        }

        @Test void tokenInvalidoNoAutenticaPeroContinuaLaCadena() throws Exception {
            when(jwtUtil.esValido("mal")).thenReturn(false);
            ejecutar("Bearer mal");
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(chain).doFilter(any(), any());
        }

        @Test void sinEncabezadoNoAutentica() throws Exception {
            ejecutar(null);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(chain).doFilter(any(), any());
            verifyNoInteractions(jwtUtil);
        }

        @Test void encabezadoSinPrefijoBearerSeIgnora() throws Exception {
            ejecutar("Basic abc");
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verifyNoInteractions(jwtUtil);
        }
    }
}
