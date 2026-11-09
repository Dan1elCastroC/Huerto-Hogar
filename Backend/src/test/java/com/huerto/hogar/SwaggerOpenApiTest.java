package com.huerto.hogar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SwaggerOpenApiTest {

    /** Rutas y métodos HTTP que debe documentar la API. */
    private static final Map<String, List<String>> RUTAS = Map.ofEntries(
        Map.entry("/api/auth/login", List.of("post")),
        Map.entry("/api/auth/registro", List.of("post")),
        Map.entry("/api/usuarios/perfil", List.of("get", "put")),
        Map.entry("/api/usuarios/perfil/contrasena", List.of("put")),
        Map.entry("/api/admin/usuarios", List.of("get", "post")),
        Map.entry("/api/admin/usuarios/{id}", List.of("get", "put", "delete")),
        Map.entry("/api/admin/usuarios/{id}/toggle-activo", List.of("patch")),
        Map.entry("/api/regiones", List.of("get")),
        Map.entry("/api/regiones/{region}/comunas", List.of("get")),
        Map.entry("/api/regiones/todas", List.of("get")),
        Map.entry("/api/v1/entities/productos", List.of("get", "post")),
        Map.entry("/api/v1/entities/productos/{id}", List.of("get", "put", "delete")),
        Map.entry("/api/v1/entities/productos/buscar", List.of("get")),
        Map.entry("/api/v1/entities/productos/categoria/{categoriaId}", List.of("get")),
        Map.entry("/api/v1/entities/productos/stock-critico", List.of("get")),
        Map.entry("/api/v1/entities/categorias", List.of("get", "post")),
        Map.entry("/api/v1/entities/categorias/{id}", List.of("get", "put", "delete")),
        Map.entry("/api/v1/entities/blogs", List.of("get", "post")),
        Map.entry("/api/v1/entities/blogs/{id}", List.of("get", "put", "delete")),
        Map.entry("/api/v1/entities/contacto", List.of("get", "post")),
        Map.entry("/api/v1/entities/contacto/{id}/leido", List.of("patch")),
        Map.entry("/api/v1/entities/cupones", List.of("get", "post")),
        Map.entry("/api/v1/entities/cupones/validar/{codigo}", List.of("get")),
        Map.entry("/api/v1/entities/cupones/{id}", List.of("delete")),
        Map.entry("/api/v1/entities/carrito", List.of("get")),
        Map.entry("/api/v1/entities/carrito/agregar", List.of("post")),
        Map.entry("/api/v1/entities/carrito/actualizar/{productoId}", List.of("put")),
        Map.entry("/api/v1/entities/carrito/eliminar/{productoId}", List.of("delete")),
        Map.entry("/api/v1/entities/carrito/vaciar", List.of("delete")),
        Map.entry("/api/v1/entities/pedidos", List.of("get")),
        Map.entry("/api/v1/entities/pedidos/checkout", List.of("post")),
        Map.entry("/api/v1/entities/pedidos/mis-pedidos", List.of("get")),
        Map.entry("/api/v1/entities/pedidos/mis-pedidos/{id}", List.of("get")),
        Map.entry("/api/v1/entities/pedidos/estado/{estado}", List.of("get")),
        Map.entry("/api/v1/entities/pedidos/{id}/estado", List.of("patch")),
        Map.entry("/api/v1/entities/reportes/resumen", List.of("get")),
        Map.entry("/api/v1/entities/reportes/stock-critico", List.of("get")),
        Map.entry("/api/v1/entities/reportes/pedidos-por-estado", List.of("get")),
        Map.entry("/api/v1/entities/reportes/productos-mas-vendidos", List.of("get")));

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    private JsonNode documento() throws Exception {
        String json = mockMvc.perform(get("/api-docs")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return mapper.readTree(json);
    }

    @Test
    void publicaDocumentoOpenApiConRutasDeLaTienda() throws Exception {
        mockMvc.perform(get("/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.openapi").exists())
            .andExpect(jsonPath("$.info.title").exists())
            .andExpect(jsonPath("$.paths['/api/v1/entities/productos']").exists());
    }

    @Test
    void exponeLaInterfazSwagger() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void documentaTodasLasRutasEsperadasConSusMetodos() throws Exception {
        JsonNode paths = documento().path("paths");
        RUTAS.forEach((ruta, metodos) -> {
            assertTrue(paths.has(ruta), "Falta la ruta en /api-docs: " + ruta);
            for (String metodo : metodos)
                assertTrue(paths.get(ruta).has(metodo), "Falta " + metodo.toUpperCase() + " " + ruta);
        });
    }

    @Test
    void noDocumentaRutasInesperadas() throws Exception {
        JsonNode paths = documento().path("paths");
        for (Iterator<String> it = paths.fieldNames(); it.hasNext(); ) {
            String ruta = it.next();
            assertTrue(RUTAS.containsKey(ruta), "Ruta documentada no contemplada en el test: " + ruta);
        }
    }

    @Test
    void cadaOperacionTieneSummaryDescripcionYRespuestas() throws Exception {
        JsonNode paths = documento().path("paths");
        int operaciones = 0;
        for (Iterator<Map.Entry<String, JsonNode>> it = paths.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> ruta = it.next();
            for (Iterator<Map.Entry<String, JsonNode>> ops = ruta.getValue().fields(); ops.hasNext(); ) {
                Map.Entry<String, JsonNode> op = ops.next();
                String id = op.getKey().toUpperCase() + " " + ruta.getKey();
                operaciones++;
                assertFalse(op.getValue().path("summary").asText().isBlank(), "Sin summary: " + id);
                assertFalse(op.getValue().path("description").asText().isBlank(), "Sin description: " + id);
                assertTrue(op.getValue().path("responses").size() >= 1, "Sin responses: " + id);
                assertTrue(op.getValue().path("tags").size() >= 1, "Sin tags: " + id);
            }
        }
        assertEquals(RUTAS.values().stream().mapToInt(List::size).sum(), operaciones);
    }

    @Test
    void existeEsquemaDeSeguridadBearerAuthJwt() throws Exception {
        JsonNode esquema = documento().at("/components/securitySchemes/bearerAuth");
        assertFalse(esquema.isMissingNode(), "No existe el esquema bearerAuth");
        assertEquals("http", esquema.path("type").asText());
        assertEquals("bearer", esquema.path("scheme").asText());
        assertEquals("JWT", esquema.path("bearerFormat").asText());
    }

    @Test
    void endpointsProtegidosExigenBearerAuthYLosPublicosNo() throws Exception {
        JsonNode paths = documento().path("paths");
        assertTrue(paths.at("/~1api~1v1~1entities~1carrito/get/security/0").has("bearerAuth"));
        assertTrue(paths.at("/~1api~1admin~1usuarios/get/security/0").has("bearerAuth"));
        assertTrue(paths.at("/~1api~1v1~1entities~1reportes~1resumen/get/security/0").has("bearerAuth"));
        assertTrue(paths.at("/~1api~1v1~1entities~1productos/post/security/0").has("bearerAuth"));
        assertTrue(paths.at("/~1api~1auth~1login/post/security").isMissingNode());
        assertTrue(paths.at("/~1api~1v1~1entities~1productos/get/security").isMissingNode());
        assertTrue(paths.at("/~1api~1regiones/get/security").isMissingNode());
    }

    @Test
    void losEsquemasDeDatosTienenDescripcionYEjemplos() throws Exception {
        JsonNode schemas = documento().at("/components/schemas");
        for (String nombre : List.of("LoginRequest", "LoginResponse", "ProductoDto", "RegistroRequest", "CheckoutRequest")) {
            assertTrue(schemas.has(nombre), "Falta el esquema " + nombre);
            JsonNode props = schemas.get(nombre).path("properties");
            assertTrue(props.size() > 0, "Sin propiedades: " + nombre);
            for (Iterator<Map.Entry<String, JsonNode>> it = props.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> p = it.next();
                assertFalse(p.getValue().path("description").asText().isBlank(), "Sin description: " + nombre + "." + p.getKey());
            }
        }
        assertEquals("cliente@huertohogar.cl", schemas.at("/LoginRequest/properties/correo/example").asText());
    }
}
