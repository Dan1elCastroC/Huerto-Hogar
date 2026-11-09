package com.huerto.hogar;

import com.huerto.hogar.auth.AuthController;
import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.security.jwt.JwtUtil;
import com.huerto.hogar.usuario.entity.Rol;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock UsuarioRepository usuarioRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @InjectMocks AuthController controller;

    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private UsuarioEntity usuario(boolean activo) {
        return UsuarioEntity.builder().id(7L).correo("cliente@gmail.com").contrasena("hash")
            .rol(Rol.CLIENTE).activo(activo).build();
    }

    private String login(String correo, String clave) {
        return "{\"correo\":\"" + correo + "\",\"contrasena\":\"" + clave + "\"}";
    }

    @Test void loginCorrectoDevuelveTokenYDatosDelUsuario() throws Exception {
        when(usuarioRepo.findByCorreo("cliente@gmail.com")).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("clave1", "hash")).thenReturn(true);
        when(jwtUtil.generarToken("cliente@gmail.com", "CLIENTE")).thenReturn("tok.en.jwt");

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(login("cliente@gmail.com", "clave1")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("tok.en.jwt"))
            .andExpect(jsonPath("$.correo").value("cliente@gmail.com"))
            .andExpect(jsonPath("$.rol").value("CLIENTE"))
            .andExpect(jsonPath("$.usuarioId").value(7));
    }

    @Test void loginConCorreoInexistenteRetorna400() throws Exception {
        when(usuarioRepo.findByCorreo("no@gmail.com")).thenReturn(Optional.empty());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(login("no@gmail.com", "clave1")))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Credenciales incorrectas"));
        verifyNoInteractions(jwtUtil);
    }

    @Test void loginConCuentaDesactivadaRetorna400() throws Exception {
        when(usuarioRepo.findByCorreo("cliente@gmail.com")).thenReturn(Optional.of(usuario(false)));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(login("cliente@gmail.com", "clave1")))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Cuenta desactivada"));
        verifyNoInteractions(passwordEncoder, jwtUtil);
    }

    @Test void loginConContrasenaIncorrectaRetorna400() throws Exception {
        when(usuarioRepo.findByCorreo("cliente@gmail.com")).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(login("cliente@gmail.com", "mala")))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Credenciales incorrectas"));
        verifyNoInteractions(jwtUtil);
    }

    @Test void loginConCuerpoInvalidoRetorna400ConDetalles() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(login("no-es-correo", "12")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detalles").isArray());
        verifyNoInteractions(usuarioRepo);
    }
}
