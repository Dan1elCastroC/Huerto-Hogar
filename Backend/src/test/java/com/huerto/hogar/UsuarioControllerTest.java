package com.huerto.hogar;

import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.usuario.controller.UsuarioController;
import com.huerto.hogar.usuario.dto.*;
import com.huerto.hogar.usuario.interfaces.IUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {
    static final Principal USER = new UsuarioPrincipal();
    static final String REGISTRO = "{\"run\":\"123456785\",\"nombre\":\"Ana\",\"apellidos\":\"Soto\","
        + "\"correo\":\"ana@gmail.com\",\"contrasena\":\"clave1\",\"confirmarContrasena\":\"clave1\","
        + "\"direccion\":\"Calle 1\",\"region\":\"Maule\",\"comuna\":\"Talca\"}";
    static final String PERFIL = "{\"nombre\":\"Ana\",\"apellidos\":\"Soto\",\"direccion\":\"Calle 1\","
        + "\"region\":\"Maule\",\"comuna\":\"Talca\"}";
    static final String ADMIN_UPD = "{\"nombre\":\"Ana\",\"apellidos\":\"Soto\",\"direccion\":\"Calle 1\","
        + "\"region\":\"Maule\",\"comuna\":\"Talca\",\"correo\":\"ana@gmail.com\",\"rol\":\"VENDEDOR\"}";

    /** Principal mínimo con el correo del usuario autenticado. */
    static class UsuarioPrincipal extends UsernamePasswordAuthenticationToken {
        UsuarioPrincipal() { super("ana@gmail.com", null); }
    }

    @Mock IUsuarioService service;
    @InjectMocks UsuarioController controller;
    MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private UsuarioResponse usuario() {
        return UsuarioResponse.builder().id(1L).correo("ana@gmail.com").nombre("Ana").rol("CLIENTE").activo(true).build();
    }

    // ── registro público ──
    @Test void registrarCreaUsuarioConStatus201() throws Exception {
        when(service.registrar(any(RegistroRequest.class))).thenReturn(usuario());
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(REGISTRO))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.correo").value("ana@gmail.com"));
    }

    @Test void registrarConDatosInvalidosRetorna400() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void registrarConReglaDeNegocioRetorna400() throws Exception {
        when(service.registrar(any(RegistroRequest.class))).thenThrow(new RuntimeException("Correo ya registrado"));
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(REGISTRO))
            .andExpect(status().isBadRequest()).andExpect(content().string("Correo ya registrado"));
    }

    // ── perfil ──
    @Test void perfilOk() throws Exception {
        when(service.findByCorreo("ana@gmail.com")).thenReturn(usuario());
        mvc.perform(get("/api/usuarios/perfil").principal(USER)).andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test void perfilInexistenteRetorna404() throws Exception {
        when(service.findByCorreo("ana@gmail.com")).thenThrow(new RuntimeException("Usuario no encontrado"));
        mvc.perform(get("/api/usuarios/perfil").principal(USER)).andExpect(status().isNotFound());
    }

    @Test void actualizarPerfilOk() throws Exception {
        when(service.actualizarPerfil(eq("ana@gmail.com"), any(ActualizarUsuarioRequest.class))).thenReturn(usuario());
        mvc.perform(put("/api/usuarios/perfil").principal(USER).contentType(MediaType.APPLICATION_JSON).content(PERFIL))
            .andExpect(status().isOk());
    }

    @Test void actualizarPerfilInvalidoRetorna400() throws Exception {
        mvc.perform(put("/api/usuarios/perfil").principal(USER).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void actualizarPerfilConErrorRetorna400() throws Exception {
        when(service.actualizarPerfil(eq("ana@gmail.com"), any(ActualizarUsuarioRequest.class)))
            .thenThrow(new RuntimeException("mal"));
        mvc.perform(put("/api/usuarios/perfil").principal(USER).contentType(MediaType.APPLICATION_JSON).content(PERFIL))
            .andExpect(status().isBadRequest());
    }

    @Test void cambiarContrasenaOk() throws Exception {
        mvc.perform(put("/api/usuarios/perfil/contrasena").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"contrasenaActual\":\"clave1\",\"nuevaContrasena\":\"clave2\",\"confirmarContrasena\":\"clave2\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Contraseña actualizada correctamente"));
        verify(service).cambiarContrasena(eq("ana@gmail.com"), any(CambioContrasenaRequest.class));
    }

    @Test void cambiarContrasenaConActualIncorrectaRetorna400() throws Exception {
        doThrow(new RuntimeException("Contraseña actual incorrecta"))
            .when(service).cambiarContrasena(eq("ana@gmail.com"), any(CambioContrasenaRequest.class));
        mvc.perform(put("/api/usuarios/perfil/contrasena").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"contrasenaActual\":\"mala\",\"nuevaContrasena\":\"clave2\",\"confirmarContrasena\":\"clave2\"}"))
            .andExpect(status().isBadRequest()).andExpect(content().string("Contraseña actual incorrecta"));
    }

    @Test void cambiarContrasenaMuyCortaRetorna400() throws Exception {
        mvc.perform(put("/api/usuarios/perfil/contrasena").principal(USER).contentType(MediaType.APPLICATION_JSON)
                .content("{\"contrasenaActual\":\"clave1\",\"nuevaContrasena\":\"12\",\"confirmarContrasena\":\"12\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    // ── admin ──
    @Test void adminFindAllOk() throws Exception {
        when(service.findAll()).thenReturn(List.of(usuario()));
        mvc.perform(get("/api/admin/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void adminFindAllConErrorRetorna400() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("mal"));
        mvc.perform(get("/api/admin/usuarios")).andExpect(status().isBadRequest());
    }

    @Test void adminFindByIdOk() throws Exception {
        when(service.findById(1L)).thenReturn(usuario());
        mvc.perform(get("/api/admin/usuarios/1")).andExpect(status().isOk());
    }

    @Test void adminFindByIdInexistenteRetorna404() throws Exception {
        when(service.findById(9L)).thenThrow(new RuntimeException("no existe"));
        mvc.perform(get("/api/admin/usuarios/9")).andExpect(status().isNotFound());
    }

    @Test void adminCrearRetorna201() throws Exception {
        when(service.crearDesdeAdmin(any(RegistroRequest.class))).thenReturn(usuario());
        mvc.perform(post("/api/admin/usuarios").contentType(MediaType.APPLICATION_JSON).content(REGISTRO))
            .andExpect(status().isCreated());
    }

    @Test void adminCrearConErrorRetorna400() throws Exception {
        when(service.crearDesdeAdmin(any(RegistroRequest.class))).thenThrow(new RuntimeException("duplicado"));
        mvc.perform(post("/api/admin/usuarios").contentType(MediaType.APPLICATION_JSON).content(REGISTRO))
            .andExpect(status().isBadRequest());
    }

    @Test void adminActualizarOk() throws Exception {
        when(service.actualizarDesdeAdmin(eq(1L), any(ActualizarUsuarioAdminRequest.class))).thenReturn(usuario());
        mvc.perform(put("/api/admin/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(ADMIN_UPD))
            .andExpect(status().isOk());
    }

    @Test void adminActualizarConErrorRetorna400() throws Exception {
        when(service.actualizarDesdeAdmin(eq(1L), any(ActualizarUsuarioAdminRequest.class)))
            .thenThrow(new RuntimeException("mal"));
        mvc.perform(put("/api/admin/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(ADMIN_UPD))
            .andExpect(status().isBadRequest());
    }

    @Test void adminToggleActivoOk() throws Exception {
        when(service.toggleActivo(1L)).thenReturn(usuario());
        mvc.perform(patch("/api/admin/usuarios/1/toggle-activo")).andExpect(status().isOk());
    }

    @Test void adminToggleActivoInexistenteRetorna404() throws Exception {
        when(service.toggleActivo(7L)).thenThrow(new RuntimeException("no existe"));
        mvc.perform(patch("/api/admin/usuarios/7/toggle-activo")).andExpect(status().isNotFound());
    }

    @Test void adminDeleteOk() throws Exception {
        mvc.perform(delete("/api/admin/usuarios/1")).andExpect(status().isOk())
            .andExpect(content().string("Usuario eliminado"));
        verify(service).deleteById(1L);
    }

    @Test void adminDeleteInexistenteRetorna404() throws Exception {
        doThrow(new RuntimeException("no existe")).when(service).deleteById(8L);
        mvc.perform(delete("/api/admin/usuarios/8")).andExpect(status().isNotFound());
    }
}
