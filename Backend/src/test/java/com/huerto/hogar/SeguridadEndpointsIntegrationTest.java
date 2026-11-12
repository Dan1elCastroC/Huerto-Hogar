package com.huerto.hogar;

import com.huerto.hogar.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        mvc.perform(get("/api/admin/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/entities/carrito")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/entities/reportes/resumen")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuarios/perfil")).andExpect(status().isUnauthorized());
    }

    @Test void tokenInvalidoEsTratadoComoAnonimo() throws Exception {
        mvc.perform(get("/api/admin/usuarios").header("Authorization", "Bearer basura"))
            .andExpect(status().isUnauthorized());
    }

    @Test void sinTokenLaRespuestaEsJsonConFormatoApiError() throws Exception {
        mvc.perform(get("/api/admin/usuarios"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.mensaje").value("Debes iniciar sesión para acceder a este recurso"));
    }

    @Test void sinRolSuficienteLaRespuestaEs403ConFormatoApiError() throws Exception {
        mvc.perform(get("/api/admin/usuarios").header("Authorization", bearer("CLIENTE")))
            .andExpect(status().isForbidden())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.mensaje").value("Sin permiso"));
    }

    @Test void anonimoEnEndpointConPreAuthorizeRecibe401() throws Exception {
        mvc.perform(get("/api/v1/entities/productos/stock-critico")).andExpect(status().isUnauthorized());
    }

    @Test void clienteEnEndpointConPreAuthorizeRecibe403() throws Exception {
        mvc.perform(get("/api/v1/entities/productos/stock-critico").header("Authorization", bearer("CLIENTE")))
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
