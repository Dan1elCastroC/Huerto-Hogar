package com.huerto.hogar;

import com.huerto.hogar.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verifica con la cadena de seguridad real (JWT) qué rol puede acceder a cada endpoint. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeguridadEndpointsIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwt;

    private String bearer(String rol) { return "Bearer " + jwt.generarToken("rol@gmail.com", rol); }

    @Test void endpointsPublicosNoRequierenToken() throws Exception {
        mvc.perform(get("/api/v1/entities/productos")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/entities/categorias")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/entities/blogs")).andExpect(status().isOk());
        mvc.perform(get("/api/regiones")).andExpect(status().isOk());
    }

    @Test void endpointsProtegidosSinTokenSonRechazados() throws Exception {
        mvc.perform(get("/api/admin/usuarios")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/entities/carrito")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/entities/reportes/resumen")).andExpect(status().isForbidden());
        mvc.perform(get("/api/usuarios/perfil")).andExpect(status().isForbidden());
    }

    @Test void tokenInvalidoEsTratadoComoAnonimo() throws Exception {
        mvc.perform(get("/api/admin/usuarios").header("Authorization", "Bearer basura"))
            .andExpect(status().isForbidden());
    }

    @Test void clienteNoPuedeUsarEndpointsDeAdministrador() throws Exception {
        mvc.perform(get("/api/admin/usuarios").header("Authorization", bearer("CLIENTE")))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/entities/reportes/resumen").header("Authorization", bearer("CLIENTE")))
            .andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/entities/productos/1").header("Authorization", bearer("CLIENTE")))
            .andExpect(status().isForbidden());
    }

    @Test void vendedorVeReportesPeroNoAdministra() throws Exception {
        mvc.perform(get("/api/v1/entities/reportes/resumen").header("Authorization", bearer("VENDEDOR")))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/entities/pedidos").header("Authorization", bearer("VENDEDOR")))
            .andExpect(status().isOk());
        mvc.perform(get("/api/admin/usuarios").header("Authorization", bearer("VENDEDOR")))
            .andExpect(status().isForbidden());
        mvc.perform(patch("/api/v1/entities/pedidos/1/estado").param("estado", "ENVIADO")
                .header("Authorization", bearer("VENDEDOR")))
            .andExpect(status().isForbidden());
    }

    @Test void administradorAccedeAlListadoDeUsuariosYReportes() throws Exception {
        mvc.perform(get("/api/admin/usuarios").header("Authorization", bearer("ADMINISTRADOR")))
            .andExpect(status().isOk());
        mvc.perform(get("/api/v1/entities/reportes/productos-mas-vendidos").header("Authorization", bearer("ADMINISTRADOR")))
            .andExpect(status().isOk());
    }
}
