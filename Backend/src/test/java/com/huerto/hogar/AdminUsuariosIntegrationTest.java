package com.huerto.hogar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMINISTRADOR")
class AdminUsuariosIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UsuarioRepository usuarios;
    @Autowired PasswordEncoder encoder;

    @Test void editarSinContrasenaConservaElHash() throws Exception {
        UsuarioEntity usuario = crearUsuario();
        String hash = usuario.getContrasena();
        mvc.perform(put("/api/admin/usuarios/" + usuario.getId()).contentType("application/json")
            .content(mapper.writeValueAsBytes(datos(usuario.getCorreo())))).andExpect(status().isOk());
        UsuarioEntity actualizado = usuarios.findById(usuario.getId()).orElseThrow();
        assertEquals("Nombre actualizado", actualizado.getNombre());
        assertEquals(hash, actualizado.getContrasena());
        assertTrue(encoder.matches("clave1", actualizado.getContrasena()));
    }

    @Test void editarRechazaContrasenaFueraDeLongitudSinAlterarHash() throws Exception {
        UsuarioEntity usuario = crearUsuario();
        for (String clave : new String[]{"12", "demasiadolarga"}) {
            Map<String, String> datos = datos(usuario.getCorreo());
            datos.put("contrasena", clave);
            datos.put("confirmarContrasena", clave);
            mvc.perform(put("/api/admin/usuarios/" + usuario.getId()).contentType("application/json")
                .content(mapper.writeValueAsBytes(datos))).andExpect(status().isBadRequest());
        }
        assertEquals(usuario.getContrasena(), usuarios.findById(usuario.getId()).orElseThrow().getContrasena());
    }

    @Test void registroSigueExigiendoContrasena() throws Exception {
        Map<String, String> datos = datos("registro@gmail.com");
        datos.put("run", "190110222");
        long cantidad = usuarios.count();
        mvc.perform(post("/api/auth/registro").contentType("application/json")
            .content(mapper.writeValueAsBytes(datos))).andExpect(status().isBadRequest());
        assertEquals(cantidad, usuarios.count());
    }

    @Test void altaAdministrativaSigueExigiendoContrasena() throws Exception {
        Map<String, String> datos = datos("alta@gmail.com");
        datos.put("run", "190110222");
        long cantidad = usuarios.count();
        mvc.perform(post("/api/admin/usuarios").contentType("application/json")
            .content(mapper.writeValueAsBytes(datos))).andExpect(status().isBadRequest());
        assertEquals(cantidad, usuarios.count());
    }

    private Map<String, String> datos(String correo) {
        return new HashMap<>(Map.of("nombre", "Nombre actualizado", "apellidos", "Prueba",
            "correo", correo, "direccion", "Calle 123", "region", "Biobío", "comuna", "Concepción", "rol", "CLIENTE"));
    }

    private UsuarioEntity crearUsuario() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        return usuarios.save(UsuarioEntity.builder().run(id.toUpperCase() + "K")
            .nombre("Nombre anterior").apellidos("Prueba").correo(id + "@gmail.com")
            .contrasena(encoder.encode("clave1")).direccion("Calle 123").region("Biobío").comuna("Concepción").build());
    }
}
